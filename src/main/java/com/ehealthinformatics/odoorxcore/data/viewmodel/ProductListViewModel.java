package com.ehealthinformatics.odoorxcore.data.viewmodel;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.odoorxcore.data.dao.DaoRepo;
import com.ehealthinformatics.odoorxcore.data.dao.ProductDao;
import com.ehealthinformatics.odoorxcore.data.dao.QueryFields;
import com.ehealthinformatics.odoorxcore.data.dto.Product;

import java.util.HashMap;
import java.util.List;

//Change to generically instantiable
public class ProductListViewModel extends ViewModel {
    private final HashMap<String, List<Product>> cache = new HashMap<>();
    private final MutableLiveData<List<Product>> productLiveData =
            new MutableLiveData<>();
    private ProductDao productDao;

    public ProductListViewModel()
    {
        DaoRepo daoRepo = DaoRepo.getInstance();
        productDao = daoRepo.getDao(ProductDao.class);
    }

    public LiveData<List<Product>> getData() {
        return productLiveData;
    }

    public void searchFilter(final String filterText, final QueryFields queryFields){

        new AsyncTask<String, Void, List<Product>>(){
            @Override
            protected List<Product> doInBackground(String... searchText) {
                return productDao.searchFilter(filterText, queryFields);
            }

            @Override
            protected void onPostExecute(List<Product> productList) {
                super.onPostExecute(productList);
                productLiveData.setValue(productList);
            }
        }.execute();
    }

}