package com.ehealthinformatics.odoorxcore.data.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.odoorxcore.core.utils.BitmapUtils;
import com.ehealthinformatics.odoorxcore.data.dao.DaoRepo;
import com.ehealthinformatics.odoorxcore.data.dao.PosOrderDao;
import com.ehealthinformatics.odoorxcore.data.dao.ProductDao;
import com.ehealthinformatics.odoorxcore.data.dto.CustomerOrderData;
import com.ehealthinformatics.odoorxcore.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerOrderViewModel extends ViewModel {
    private final HashMap<String, CustomerOrderData> cache = new HashMap<>();
    private final MutableLiveData<CustomerOrderData> productLiveData =
            new MutableLiveData<>();
    private ProductDao productDao;
    private PosOrderDao posOrderDao;
    private Context context;

    public CustomerOrderViewModel()
    {
        DaoRepo daoRepo = DaoRepo.getInstance();
        productDao = daoRepo.getDao(ProductDao.class);
        posOrderDao = daoRepo.getDao(PosOrderDao.class);
        this.context = context;
    }

    public LiveData<CustomerOrderData> getData() {
        return productLiveData;
    }

    public void loadOrderData(){

        new AsyncTask<Void, Void, CustomerOrderData>(){
            @Override
            protected CustomerOrderData doInBackground(Void... voids) {
                ArrayList<Bitmap> adverts = new ArrayList<>();
                Bitmap advert1 = BitmapUtils.getBitmap(context, R.drawable.advert1);
                Bitmap advert2 = BitmapUtils.getBitmap(context, R.drawable.advert2);
                Bitmap advert3 = BitmapUtils.getBitmap(context, R.drawable.advert3);
                adverts.add(advert1);
                adverts.add(advert2);
                adverts.add(advert3);
                return new CustomerOrderData(adverts);
            }

            @Override
            protected void onPostExecute(CustomerOrderData data) {
                    super.onPostExecute(data);
                    productLiveData.setValue(data);

            }
        }.execute();
    }

}