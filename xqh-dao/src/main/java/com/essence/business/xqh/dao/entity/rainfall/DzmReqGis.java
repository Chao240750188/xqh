package com.essence.business.xqh.dao.entity.rainfall;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName DzmReqGis
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/6/27 15:42
 * @Version 1.0
 **/
public class DzmReqGis implements Serializable {

    /**
     * displayFieldName :
     * fieldAliases : {"FID":"FID","stcd":"stcd","stnm":"stnm","lgtd":"lgtd","lttd":"lttd","drp":"drp"}
     * geometryType : esriGeometryPoint
     * spatialReference : {"wkid":4326,"latestWkid":4326}
     * fields : [{"name":"FID","type":"esriFieldTypeOID","alias":"FID"},{"name":"stcd","type":"esriFieldTypeDouble","alias":"stcd"},{"name":"stnm","type":"esriFieldTypeString","alias":"stnm","length":254},{"name":"lgtd","type":"esriFieldTypeDouble","alias":"lgtd"},{"name":"lttd","type":"esriFieldTypeDouble","alias":"lttd"},{"name":"drp","type":"esriFieldTypeDouble","alias":"drp"}]
     * features : [{"attributes":{"FID":0,"stcd":2,"stnm":"上庄闸","lgtd":116.208453,"lttd":40.101636,"drp":13},"geometry":{"x":116.208453,"y":40.101636}},{"attributes":{"FID":1,"stcd":3,"stnm":"苏家坨","lgtd":116.164178,"lttd":40.078311,"drp":15},"geometry":{"x":116.164178,"y":40.078311}},{"attributes":{"FID":2,"stcd":17,"stnm":"五七水库","lgtd":116.085631,"lttd":40.085131,"drp":14},"geometry":{"x":116.085631,"y":40.085131}},{"attributes":{"FID":3,"stcd":5,"stnm":"四季青","lgtd":116.210339,"lttd":39.96825,"drp":15},"geometry":{"x":116.210339,"y":39.96825}},{"attributes":{"FID":4,"stcd":6,"stnm":"锦绣大地","lgtd":116.223561,"lttd":39.937296,"drp":15},"geometry":{"x":116.223561,"y":39.937296}},{"attributes":{"FID":5,"stcd":7,"stnm":"西三旗","lgtd":116.360831,"lttd":40.034339,"drp":2},"geometry":{"x":116.360831,"y":40.034339}},{"attributes":{"FID":6,"stcd":8,"stnm":"甘家口","lgtd":116.321386,"lttd":39.920728,"drp":16},"geometry":{"x":116.321386,"y":39.920728}},{"attributes":{"FID":7,"stcd":9,"stnm":"温泉","lgtd":116.187131,"lttd":40.028214,"drp":16},"geometry":{"x":116.187131,"y":40.028214}},{"attributes":{"FID":8,"stcd":10,"stnm":"学院路","lgtd":116.339431,"lttd":39.997986,"drp":15},"geometry":{"x":116.339431,"y":39.997986}},{"attributes":{"FID":9,"stcd":11,"stnm":"马连洼","lgtd":116.275306,"lttd":40.032217,"drp":6},"geometry":{"x":116.275306,"y":40.032217}},{"attributes":{"FID":10,"stcd":12,"stnm":"永丰园","lgtd":116.235831,"lttd":40.072961,"drp":17},"geometry":{"x":116.235831,"y":40.072961}},{"attributes":{"FID":11,"stcd":13,"stnm":"香山干休所","lgtd":116.198147,"lttd":39.982114,"drp":12},"geometry":{"x":116.198147,"y":39.982114}},{"attributes":{"FID":12,"stcd":14,"stnm":"北太平庄","lgtd":116.361869,"lttd":39.950819,"drp":14},"geometry":{"x":116.361869,"y":39.950819}},{"attributes":{"FID":13,"stcd":15,"stnm":"万寿路","lgtd":116.261983,"lttd":39.905028,"drp":15},"geometry":{"x":116.261983,"y":39.905028}},{"attributes":{"FID":14,"stcd":18,"stnm":"百望山","lgtd":116.256156,"lttd":40.038147,"drp":6},"geometry":{"x":116.256156,"y":40.038147}},{"attributes":{"FID":15,"stcd":19,"stnm":"万泉庄","lgtd":116.295947,"lttd":39.957767,"drp":16},"geometry":{"x":116.295947,"y":39.957767}},{"attributes":{"FID":16,"stcd":21,"stnm":"花园路","lgtd":116.372419,"lttd":39.978317,"drp":15},"geometry":{"x":116.372419,"y":39.978317}},{"attributes":{"FID":17,"stcd":22,"stnm":"八里庄","lgtd":116.301492,"lttd":39.926958,"drp":16},"geometry":{"x":116.301492,"y":39.926958}},{"attributes":{"FID":18,"stcd":16,"stnm":"西玉河","lgtd":116.226094,"lttd":40.096881,"drp":13},"geometry":{"x":116.226094,"y":40.096881}},{"attributes":{"FID":19,"stcd":23,"stnm":"香山街道","lgtd":116.207231,"lttd":39.993672,"drp":12},"geometry":{"x":116.207231,"y":39.993672}},{"attributes":{"FID":20,"stcd":25,"stnm":"北下关","lgtd":116.338911,"lttd":39.958197,"drp":15},"geometry":{"x":116.338911,"y":39.958197}},{"attributes":{"FID":21,"stcd":20,"stnm":"北坞村","lgtd":116.251811,"lttd":39.975067,"drp":17},"geometry":{"x":116.251811,"y":39.975067}},{"attributes":{"FID":22,"stcd":4,"stnm":"上地","lgtd":116.295481,"lttd":40.042136,"drp":2},"geometry":{"x":116.295481,"y":40.042136}},{"attributes":{"FID":23,"stcd":26,"stnm":"田村农业观光园","lgtd":116.25,"lttd":39.936389,"drp":18},"geometry":{"x":116.25,"y":39.936389}},{"attributes":{"FID":24,"stcd":24,"stnm":"田村绿化带","lgtd":116.245958,"lttd":39.922767,"drp":14},"geometry":{"x":116.245958,"y":39.922767}},{"attributes":{"FID":25,"stcd":30,"stnm":"金河闸水位站","lgtd":116.274236,"lttd":39.972372,"drp":18},"geometry":{"x":116.274236,"y":39.972372}},{"attributes":{"FID":26,"stcd":31,"stnm":"北钢闸水位站","lgtd":116.327242,"lttd":40.014839,"drp":6},"geometry":{"x":116.327242,"y":40.014839}},{"attributes":{"FID":27,"stcd":32,"stnm":"稻香湖桥水位站","lgtd":116.154469,"lttd":40.096708,"drp":33},"geometry":{"x":116.154469,"y":40.096708}},{"attributes":{"FID":28,"stcd":33,"stnm":"北长河水位站","lgtd":116.231597,"lttd":39.997939,"drp":16.5},"geometry":{"x":116.231597,"y":39.997939}},{"attributes":{"FID":29,"stcd":34,"stnm":"周家巷沟水位站","lgtd":116.164736,"lttd":40.069161,"drp":33},"geometry":{"x":116.164736,"y":40.069161}}]
     */

    private String displayFieldName;
    private FieldAliasesBean fieldAliases;
    private String geometryType;
    private SpatialReferenceBean spatialReference;
    private List<FieldsBean> fields;
    private List<FeaturesBean> features;

    public String getDisplayFieldName() {
        return displayFieldName;
    }

    public void setDisplayFieldName(String displayFieldName) {
        this.displayFieldName = displayFieldName;
    }

    public FieldAliasesBean getFieldAliases() {
        return fieldAliases;
    }

    public void setFieldAliases(FieldAliasesBean fieldAliases) {
        this.fieldAliases = fieldAliases;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public SpatialReferenceBean getSpatialReference() {
        return spatialReference;
    }

    public void setSpatialReference(SpatialReferenceBean spatialReference) {
        this.spatialReference = spatialReference;
    }

    public List<FieldsBean> getFields() {
        return fields;
    }

    public void setFields(List<FieldsBean> fields) {
        this.fields = fields;
    }

    public List<FeaturesBean> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeaturesBean> features) {
        this.features = features;
    }

    public static class FieldAliasesBean {
        /**
         * FID : FID
         * stcd : stcd
         * stnm : stnm
         * lgtd : lgtd
         * lttd : lttd
         * drp : drp
         */

        private String FID;
        private String stcd;
        private String stnm;
        private String lgtd;
        private String lttd;
        private String drp;

        public String getFID() {
            return FID;
        }

        public void setFID(String FID) {
            this.FID = FID;
        }

        public String getStcd() {
            return stcd;
        }

        public void setStcd(String stcd) {
            this.stcd = stcd;
        }

        public String getStnm() {
            return stnm;
        }

        public void setStnm(String stnm) {
            this.stnm = stnm;
        }

        public String getLgtd() {
            return lgtd;
        }

        public void setLgtd(String lgtd) {
            this.lgtd = lgtd;
        }

        public String getLttd() {
            return lttd;
        }

        public void setLttd(String lttd) {
            this.lttd = lttd;
        }

        public String getDrp() {
            return drp;
        }

        public void setDrp(String drp) {
            this.drp = drp;
        }
    }

    public static class SpatialReferenceBean {
        /**
         * wkid : 4326
         * latestWkid : 4326
         */

        private int wkid;
        private int latestWkid;

        public int getWkid() {
            return wkid;
        }

        public void setWkid(int wkid) {
            this.wkid = wkid;
        }

        public int getLatestWkid() {
            return latestWkid;
        }

        public void setLatestWkid(int latestWkid) {
            this.latestWkid = latestWkid;
        }

        public SpatialReferenceBean(int wkid, int latestWkid) {
            this.wkid = wkid;
            this.latestWkid = latestWkid;
        }
    }

    public static class FieldsBean {
        /**
         * name : FID
         * type : esriFieldTypeOID
         * alias : FID
         * length : 254
         */

        private String name;
        private String type;
        private String alias;
        private int length;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public FieldsBean(String name, String type, String alias, int length) {
            this.name = name;
            this.type = type;
            this.alias = alias;
            this.length = length;
        }

        public FieldsBean(String name, String type, String alias) {
            this.name = name;
            this.type = type;
            this.alias = alias;
        }
    }

    public static class FeaturesBean {
        /**
         * attributes : {"FID":0,"stcd":2,"stnm":"上庄闸","lgtd":116.208453,"lttd":40.101636,"drp":13}
         * geometry : {"x":116.208453,"y":40.101636}
         */

        private AttributesBean attributes;
        private GeometryBean geometry;

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public GeometryBean getGeometry() {
            return geometry;
        }

        public void setGeometry(GeometryBean geometry) {
            this.geometry = geometry;
        }

        public static class AttributesBean {
            /**
             * FID : 0
             * stcd : 2
             * stnm : 上庄闸
             * lgtd : 116.208453
             * lttd : 40.101636
             * drp : 13
             */

            private int FID;
            private String stcd;
            private String stnm;
            private double lgtd;
            private double lttd;
            private double drp;

            public int getFID() {
                return FID;
            }

            public void setFID(int FID) {
                this.FID = FID;
            }

            public String getStcd() {
                return stcd;
            }

            public void setStcd(String stcd) {
                this.stcd = stcd;
            }

            public String getStnm() {
                return stnm;
            }

            public void setStnm(String stnm) {
                this.stnm = stnm;
            }

            public double getLgtd() {
                return lgtd;
            }

            public void setLgtd(double lgtd) {
                this.lgtd = lgtd;
            }

            public double getLttd() {
                return lttd;
            }

            public void setLttd(double lttd) {
                this.lttd = lttd;
            }

            public double getDrp() {
                return drp;
            }

            public void setDrp(double drp) {
                this.drp = drp;
            }
        }

        public static class GeometryBean {
            /**
             * x : 116.208453
             * y : 40.101636
             */

            private double x;
            private double y;

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }
        }
    }
}
