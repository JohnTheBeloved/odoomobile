package com.ehealthinformatics.odoorxcore.data.dao;

import android.content.Context;


import com.ehealthinformatics.odoorxcore.core.orm.ODataRow;
import com.ehealthinformatics.odoorxcore.core.orm.OModel;
import com.ehealthinformatics.odoorxcore.core.orm.fields.OColumn;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OBoolean;
import com.ehealthinformatics.odoorxcore.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.odoorxcore.core.support.OUser;
import com.ehealthinformatics.odoorxcore.data.db.Columns;
import com.ehealthinformatics.odoorxcore.data.db.ModelNames;
import com.ehealthinformatics.odoorxcore.data.dto.Category;
import com.ehealthinformatics.odoorxcore.data.dto.ProductImage;
import com.ehealthinformatics.odoorxcore.data.dto.ProductTemplate;
import com.ehealthinformatics.odoorxcore.data.dto.Uom;

import java.util.List;

import static com.ehealthinformatics.odoorxcore.data.db.Columns.ProductTemplateCol;

public class ProductTemplateDao extends OModel {

    OColumn name = new OColumn(Columns.name, OVarchar.class);
    OColumn description = new OColumn( Columns.description, OVarchar.class);
    OColumn active = new OColumn(Columns.active, OBoolean.class);
    OColumn uom_id = new OColumn("UOM", UoMDao.class, OColumn.RelationType.ManyToOne);
    OColumn categ_id = new OColumn("Category", CategoryDao.class, OColumn.RelationType.ManyToOne);
    OColumn is_medicine = new OColumn("Is Medicine", OBoolean.class);
    OColumn product_image_ids = new OColumn("Website Images", ProductImageDao.class,  OColumn.RelationType.OneToMany)
    .setRelatedColumn("product_tmpl_id");

    public ProductTemplateDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT_TEMPLATE, user);
    }

    private UoMDao uoMDao;
    private CategoryDao categoryDao;
    private ProductImageDao productImageDao;

    @Override
    public void initDaos() {
        DaoRepo daoRepo = DaoRepo.getInstance(getContext());
        super.initDaos();
        uoMDao = daoRepo.getDao(UoMDao.class);
        categoryDao = daoRepo.getDao(CategoryDao.class);
        productImageDao = daoRepo.getDao(ProductImageDao.class);
    }

    public ProductTemplate get(int id, QueryFields queryFields){
        return fromRow(browse(id), queryFields);
    }

    public ProductTemplate fromRow(ODataRow row, QueryFields queryFields){
        Integer id = null, serverId = null;
        String name = null;
        Boolean active = null;
        Boolean isMedicine = null;
        String productType = null;
        Uom uom = null;
        Category category = null;
        List<ProductImage> productImages = null;
        if(queryFields.contains(Columns.id)) id = row.getInt(Columns.id);
        if(queryFields.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(queryFields.contains(Columns.name)) name = row.getString(Columns.name);
        if(queryFields.contains(Columns.active))  active = row.getBoolean(Columns.active);
        if(queryFields.contains(ProductTemplateCol.is_medicine))  isMedicine = row.getBoolean(ProductTemplateCol.is_medicine);
        if(queryFields.contains(ProductTemplateCol.product_type)) productType = row.getString(ProductTemplateCol.product_type);
        if(queryFields.contains(ProductTemplateCol.uom_id)) uom = uoMDao.fromRow(row.getM2ORecord(ProductTemplateCol.uom_id).browse(), queryFields.childField(ProductTemplateCol.uom_id));
        if(queryFields.contains(ProductTemplateCol.category_id)) category = categoryDao.fromRow(row.getM2ORecord(ProductTemplateCol.category_id).browse(), queryFields.childField(ProductTemplateCol.category_id));
        productImages = productImageDao.getImages(id);
        ProductTemplate productTemplate = new ProductTemplate(id, name, active, isMedicine, productType, uom, category, productImages);
        return  productTemplate;
    }

}
