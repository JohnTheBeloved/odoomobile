package com.ehealthinformatics.odoorxcore.data.viewmodel;


import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.odoorxcore.data.dao.DaoRepo;
import com.ehealthinformatics.odoorxcore.data.dao.PurchaseOrderDao;
import com.ehealthinformatics.odoorxcore.data.dao.QueryFields;
import com.ehealthinformatics.odoorxcore.data.dto.PurchaseOrder;

import java.util.List;

public class PurchaseViewModel extends ViewModel {
    private PurchaseOrderDao purchaseOrderDao;
    private  MutableLiveData<List<PurchaseOrder>> current = new MutableLiveData<>();

    public PurchaseViewModel(){
        DaoRepo daoRepo = DaoRepo.getInstance();
        this.purchaseOrderDao = daoRepo.getDao(PurchaseOrderDao.class);
    }

    public void loadData() {
        new AsyncTask<Void,Void, List<PurchaseOrder>>() {
            @Override
            protected List<PurchaseOrder> doInBackground(Void... voids) {
                List<PurchaseOrder> purchaseOrders;
                purchaseOrders = purchaseOrderDao.selectAllPurchaseOrder(QueryFields.all());
                return purchaseOrders;
            }
            @Override
            protected void onPostExecute(List<PurchaseOrder> data) {
               current.setValue(data);
            }
        }.execute();
    }

    public List<PurchaseOrder> getData() {
        return current.getValue();
    }

}