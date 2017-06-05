package com.phoenix.readily.business;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.phoenix.readily.R;
import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.entity.Payout;
import com.phoenix.readily.entity.Statistics;
import com.phoenix.readily.utils.DateUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by flashing on 2017/6/2.
 */

public class StatisticsBusiness extends BaseBusiness {
    public static final String SDCARD_PATH =
            Environment.getExternalStorageDirectory().getPath()
            + "/Readily/Export/";
    private PayoutBusiness payoutBusiness;
    private UserBusiness userBusiness;
    private AccountBookBusiness accountBookBusiness;

    public StatisticsBusiness(Context context) {
        super(context);
        payoutBusiness = new PayoutBusiness(context);
        userBusiness = new UserBusiness(context);
        accountBookBusiness = new AccountBookBusiness(context);
    }

    //得到拆分好的统计信息
    private List<Statistics> getStatisticsList(String condition){
        List<Payout> payoutList = payoutBusiness.
                getPayouOrderByPayoutUserId(condition);
        //获取计算方式数组
        String[] payoutTypeArray = context.getResources().
                getStringArray(R.array.PayoutType);
        List<Statistics> statisticsList = new ArrayList<>();
        if (payoutList != null){
            //遍历消费记录列表
            for (int i = 0; i < payoutList.size(); i++) {
                //取出一条消费记录
                Payout payout = payoutList.get(i);
                //将消费人ID转换为真实名称
                String[] payoutUserName = userBusiness.
                        getUserNameByUserId(payout.
                                getPayoutUserId()).split(",");//人名数组
                String[] payoutUserId = payout.getPayoutUserId()
                        .split(",");//人员ID数组
                //取出当前消费记录的计算方式
                String payoutType = payout.getPayoutType();
                //存放计算后的消费金额
                BigDecimal cost;
                //判断本次消费记录的消费类型
                if (payoutType.equals(payoutTypeArray[0])){//均分
                    //得到消费人数
                    int payoutTotal = payoutUserName.length;
                    /**
                     * 得到计算后的平均消费金额
                     * divide表示除法，2表示精确到小数点后2位
                     * ROUND_HALF_EVEN表示四舍五入
                     * 应用场景：1000元/年，按月算1000/12=83.333...
                     */
                    cost = payout.getAmount().divide(new BigDecimal(payoutTotal),
                            2, BigDecimal.ROUND_HALF_EVEN);
                }else {//借贷或者个人消费
                    cost = payout.getAmount();
                }

                //遍历这条消费记录的所有消费人的数组
                for (int j = 0; j < payoutUserId.length; j++) {
                    //如果是借贷则跳过第一个索引，因为第一个人是借贷人自己
                    if (payoutType.equals(payoutTypeArray[1]) && j==0){
                        continue;
                    }
                    //声明一个统计类
                    Statistics statistics = new Statistics();
                    //将统计类的支付人设置为消费人数组的第一个人
                    statistics.payerUserId = payoutUserName[0];
                    //设置消费人
                    statistics.consumerUserId = payoutUserName[j];
                    //设置消费类型
                    statistics.payoutType = payoutType;
                    //设置算好的消费金额
                    statistics.cost = cost;
                    //将每条消费记录拆分成如下的形式并放入集合
                    //王小强   王小强     均分  10元
                    //王小强   小李       均分  10元
                    statisticsList.add(statistics);
                }
            }
        }
        return statisticsList;
    }

    //得到总统计结果的集合
    public List<Statistics> getPayoutUserId(String condition){
        //得到拆分好的统计信息
        List<Statistics> list = getStatisticsList(condition);
        //存放按付款人分类的临时统计信息
        List<Statistics> listTemp = new ArrayList<>();
        //存放统计好的汇总
        List<Statistics> totalList = new ArrayList<>();
        String result = "";//方便看里面的值
        //遍历拆分好的统计信息
        for (int i = 0; i < list.size(); i++) {
            //得到拆分好的一条信息
            Statistics statistics = list.get(i);
            result += statistics.payerUserId+"#"
                    +statistics.consumerUserId+"#"
                    +statistics.cost+"\r\n";
            Log.e("TAG", "result--------->" + result);
            //保存当前的付款人ID
            String currentPayerUserId = statistics.payerUserId;

            //把当前信息按付款人分类的临时数据
            Statistics statisticsTemp = new Statistics();
            statisticsTemp.payerUserId = statistics.payerUserId;
            statisticsTemp.consumerUserId = statistics.consumerUserId;
            statisticsTemp.cost = statistics.cost;
            statisticsTemp.payoutType = statistics.payoutType;
            listTemp.add(statisticsTemp);

            //计算下一行的索引
            int nextIndex;
            //如果下一行索引小于统计信息索引，则加1
            if ((i+1) < list.size()){
                nextIndex = i+1;
            }
            //否则证明已经到尾，则索引赋值为当前行
            else {
                nextIndex = i;
            }

            //如果当前付款人与下一个付款人不同，证明分类统计已经到尾
            //或者已经循环到统计数组最后一位，就开始进行统计
            if (!currentPayerUserId.equals(list.get(nextIndex).payerUserId)
                     || nextIndex == i){
                //开始遍历，进行当前分类统计数组的统计
                for (int j = 0; j < listTemp.size(); j++) {
                    //取出一个统计信息
                    Statistics statisticsTotal = listTemp.get(j);
                    //判断在总统计数组当中是否已经存在该付款人和消费人的信息
                    int index = getPositionByConsumerUserId(totalList,
                            statisticsTotal.payerUserId,
                            statisticsTotal.consumerUserId);
                    //如果是后几条，已经存在，则在原来的数据上进行累加
                    if (index != -1){
                        //add表示加法
                        totalList.get(index).cost = totalList.get(index).cost
                                .add(statisticsTotal.cost);
                    }else {
                        //否则就是一条新信息，添加到统计数组当中
                        totalList.add(statisticsTotal);
                    }
                }
                //全部遍历后清空当前分类统计数组，进入下一个分类统计数组的计算
                listTemp.clear();
            }
        }
        return totalList;
    }

    //判断在总统计数组当中是否已经存在该付款人和消费人的信息
    private int getPositionByConsumerUserId(
            List<Statistics> totalList, String payerUserId,
            String consumerUserId){
        int index = -1;
        for (int i = 0; i < totalList.size(); i++) {
            if (totalList.get(i).payerUserId.equals(payerUserId) &&
                    totalList.get(i).consumerUserId.equals(consumerUserId)){
                index = i;
                break;
            }
        }
        return index;
    }

    public String getPayoutUserIdByAccountBookId(int accountBookId){
        String result = "";
        //得到一个总统计结果的集合
        List<Statistics> totalList = getPayoutUserId(
                " and accountBookId="+accountBookId);
        //将得到的信息进行转换，方便观看
        for (int i = 0; i < totalList.size(); i++) {
            Statistics statistics = totalList.get(i);
            if ("个人".equals(statistics.payoutType)){
                result += statistics.payerUserId + "个人消费" +
                        statistics.cost.toString() + "元\r\n";
            }else if ("均分".equals(statistics.payoutType)){
                if (statistics.payerUserId.equals(
                        statistics.consumerUserId)){
                    result += statistics.payerUserId + "个人消费" +
                            statistics.cost.toString() + "元\r\n";
                }else {
                    result += statistics.consumerUserId + "应支付给" +
                            statistics.payerUserId +
                            statistics.cost.toString() + "元\r\n";
                }
            }else if ("借贷".equals(statistics.payoutType)){
                result += statistics.consumerUserId + "应支付给" +
                        statistics.payerUserId +
                        statistics.cost.toString() + "元\r\n";
            }
        }
        return result;
    }

    public String exportStatistics(int accountBookId) throws Exception{
        String result = "";
        //判断是否有外存储设备
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            //取出账本名称
            String accountBookName = accountBookBusiness.
                    getAccountBookNameByAccountId(accountBookId);
            String fileName = accountBookName +
                    DateUtil.getFormatDate("yyyyMMdd") + ".xls";
            Log.e("TAG", "fileName--------->" + SDCARD_PATH + fileName);

            File fileDir = new File(SDCARD_PATH);
            if (!fileDir.exists()){
                fileDir.mkdirs();
            }
            File file = new File(SDCARD_PATH + fileName);
            if (!file.exists()){
                file.createNewFile();
            }
            //声明一个可写的表对象
            WritableWorkbook workBookData;
            //创建工作簿
            workBookData = Workbook.createWorkbook(file);//需要告诉它往哪个文件里面创建
            //创建工作表，相当于Excel的Sheet1，索引从0开始
            WritableSheet wsAccountBook = workBookData.createSheet(
                    accountBookName, 0);
            //声明表头数组
            String[] titles = {"编号","姓名","金额","消费信息","消费类型"};
            //声明一个文本标签
            Label label;
            //添加标题行
            for (int i = 0; i < titles.length; i++) {
                //参数1列，2行，3内容
                label = new Label(i, 0, titles[i]);
                //将文本标签填入一个单元格
                wsAccountBook.addCell(label);
            }

            //添加行
            //取出统计数据
            List<Statistics> totalList = getPayoutUserId(
                    " and accountBookId="+accountBookId);
            for (int i = 0; i < totalList.size(); i++) {
                //取出一条统计数据
                Statistics statistics = totalList.get(i);

                //添加编号列，数字标签，参数列，行，内容
                Number idCell = new Number(0, i+1, i+1);
                wsAccountBook.addCell(idCell);

                //添加姓名
                Label nameLabel = new Label(1, i+1, statistics.payerUserId);
                wsAccountBook.addCell(nameLabel);

                //格式化金额类型显示，#.##表示格式化为小数点后2位
                NumberFormat moneyFormat = new NumberFormat("#.##");
                WritableCellFormat wcf = new WritableCellFormat(moneyFormat);
                //添加金额
                Number costCell = new Number(2, i+1,
                        statistics.cost.doubleValue(), wcf);
                wsAccountBook.addCell(costCell);

                //添加消费信息
                String info = "";
                if ("个人".equals(statistics.payoutType)){
                    info += statistics.payerUserId + "个人消费" +
                            statistics.cost.toString() + "元\r\n";
                }else if ("均分".equals(statistics.payoutType)){
                    if (statistics.payerUserId.equals(
                            statistics.consumerUserId)){
                        info += statistics.payerUserId + "个人消费" +
                                statistics.cost.toString() + "元\r\n";
                    }else {
                        info += statistics.consumerUserId + "应支付给" +
                                statistics.payerUserId +
                                statistics.cost.toString() + "元\r\n";
                    }
                }else if ("借贷".equals(statistics.payoutType)){
                    info += statistics.consumerUserId + "应支付给" +
                            statistics.payerUserId +
                            statistics.cost.toString() + "元\r\n";
                }
                Label infoLabel = new Label(3, i+1, info);
                wsAccountBook.addCell(infoLabel);

                //添加消费类型
                Label payoutTypeLabel = new Label(4, i+1,
                        statistics.payoutType);
                wsAccountBook.addCell(payoutTypeLabel);
            }
            //写入SD卡
            workBookData.write();
            workBookData.close();
            result = "数据已经导出！位置在：" + SDCARD_PATH + fileName;
        }else {
            result = "抱歉！未检测到SD卡，数据无法导出。";
        }
        return result;
    }
}
