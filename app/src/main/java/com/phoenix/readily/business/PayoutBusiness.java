package com.phoenix.readily.business;

import android.content.Context;

import com.phoenix.readily.business.base.BaseBusiness;
import com.phoenix.readily.database.dao.PayoutDAO;
import com.phoenix.readily.entity.Payout;

/**
 * Created by flashing on 2017/5/25.
 */

public class PayoutBusiness extends BaseBusiness {
    private PayoutDAO payoutDAO;

    public PayoutBusiness(Context context) {
        super(context);
        payoutDAO = new PayoutDAO(context);
    }

    public boolean insertPayout(Payout payout){
        return payoutDAO.insertPayout(payout);
    }

    public boolean updatePayout(Payout payout){
        String condition = " payoutId="+payout.getPayoutId();
        boolean result = payoutDAO.updatePayout(condition, payout);
        return result;
    }
}
