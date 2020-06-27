package com.ehealthinformatics.odoorxcore.data.viewmodel;


import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.odoorxcore.core.orm.OValues;
import com.ehealthinformatics.odoorxcore.core.orm.RelValues;
import com.ehealthinformatics.odoorxcore.core.rpc.helper.ODomain;
import com.ehealthinformatics.odoorxcore.data.dao.CategoryDao;
import com.ehealthinformatics.odoorxcore.data.dao.DaoRepo;
import com.ehealthinformatics.odoorxcore.data.dao.ProductDao;
import com.ehealthinformatics.odoorxcore.data.dao.ProductTemplateDao;
import com.ehealthinformatics.odoorxcore.data.dao.QueryFields;
import com.ehealthinformatics.odoorxcore.data.dao.UoMDao;
import com.ehealthinformatics.odoorxcore.data.db.Columns;
import com.ehealthinformatics.odoorxcore.data.dto.Category;
import com.ehealthinformatics.odoorxcore.data.dto.Product;
import com.ehealthinformatics.odoorxcore.data.dto.Uom;
import com.ehealthinformatics.odoorxcore.data.util.SaveState;

import java.util.List;

public class ProductViewModel extends ViewModel {

    public static class ProductViewClass {
        public Product product;
        public List<Category> categories;
        public List<Uom> uoms;
    }
    private ProductDao productDao;
    private ProductTemplateDao productTemplateDao;
    private CategoryDao categoryDao;
    private UoMDao uoMDao;
    private final MutableLiveData<ProductViewClass> selected = new MutableLiveData<>();
    private final MutableLiveData<SaveState> saved = new MutableLiveData<>();
    private final MutableLiveData<Boolean> synced = new MutableLiveData<>();

    public ProductViewModel(){
        DaoRepo daoRepo = DaoRepo.getInstance();
        this.productDao = daoRepo.getDao(ProductDao.class);
        this.categoryDao = daoRepo.getDao(CategoryDao.class);
        this.productTemplateDao = daoRepo.getDao(ProductTemplateDao.class);
        this.uoMDao = daoRepo.getDao(UoMDao.class);
    }

    public void loadData(Integer id) {
        new AsyncTask<Integer,Void, ProductViewClass>() {
            @Override
            protected ProductViewClass doInBackground(Integer... ids) {
                Product product;
                List<Category> categories;
                List<Uom> uoms;
                product = productDao.get(ids[0],  QueryFields.all());
                categories = categoryDao.getCategoryList();
                uoms = uoMDao.getUOMList();
                ProductViewClass pvc = new ProductViewClass();
                pvc.product = product;
                pvc.categories = categories;
                pvc.uoms = uoms;
                return pvc;
            }
            @Override
            protected void onPostExecute(ProductViewClass data) {
                selected.setValue(data);
            }
        }.execute(id);
    }

    public void save(OValues product, OValues productTemplate) {
        new AsyncTask<OValues, Void, SaveState>() {
                @Override
                protected SaveState doInBackground(OValues... oValues) {
                    RelValues relValues = new RelValues();
                    OValues product = oValues[0];
                    OValues productTemplate = oValues[1];
                    Product currentProduct = selected.getValue().product;
                    boolean saveProduct = product.keys().size() > 0;
                    boolean saveProductTemplate = productTemplate.keys().size() > 0;
                    boolean productSaved = false;
                    boolean productTemplateSaved = false;
                    if (saveProduct)
                        productSaved = productDao.update(currentProduct.getId(), product);
                    if(saveProductTemplate)
                        productTemplateSaved = productTemplateDao.update(currentProduct.getProductTemplate().getId(), productTemplate);
                    if (saveProduct || saveProductTemplate) {
                        if (saveProductTemplate && productTemplateSaved == false) {
                            return SaveState.getErrorState("Product Template not save succesfully");
                        }
                        if (saveProduct && productSaved == false) {
                            return SaveState.getErrorState("Product not save succesfully");
                        }
                        return SaveState.getSavedState();
                    } else {
                        return SaveState.getEmptyDataState();
                    }
                }
            @Override
            protected void onPostExecute(SaveState data) {
                saved.setValue(data);
            }
        }.execute(product, productTemplate);
    }

    public void sync() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... oValues) {
                ODomain oDomain = new ODomain();
                oDomain.add(Columns.server_id, "=", selected.getValue().product.getServerId());
                 productDao.quickSyncRecords(oDomain);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean data) {
                synced.setValue(data);
            }
        }.execute();
    }

    public LiveData<ProductViewClass> getSelected() {
        return selected;
    }
    public LiveData<SaveState> getSaveStatus() {
        return saved;
    }
    public LiveData<Boolean> getSyncStatus() {
        return synced;
    }

}