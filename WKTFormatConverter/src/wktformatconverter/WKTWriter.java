package javaapplication2;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;

public class WKTWriter {
    
    // WKT formatında string değer oluştururken ortak olarak kullanılacak değerler.
    private static final String EMPTY = "EMPTY";
    private static final String VIRGUL = ",";
    private static final String AC_PARANTEZ = "(";
    private static final String KAPAT_PARANTEZ = ")";
    
    //Geometry türlerinin WKT formatındaki isimleri.
    private static final String POINT = "POINT";
    private static final String MULTIPOINT = "MULTIPOINT";
    private static final String LINESTRING = "LINESTRING";
    private static final String MULTILINESTRING = "MULTILINESTRING";
    private static final String POLYGON = "POLYGON";
    private static final String MULTIPOLYGON = "MULTIPOLYGON";
    private static final String GEOMETRYCOLLECTION = "GEOMETRYCOLLECTION";

    
    /**
     * Transforms the input Geometry object into WKT-formatted String. e.g.
     * <pre><code>
     * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
     * //returns "LINESTRING (30 10, 10 30, 40 40)"
     * </code></pre>
     */
    
    /*
     * write fonksiyonunda parametre olarak Geometry sınıfından bir nesne alındı.
     * Geometry sınıfını kalıtım alan sınıflar upcasting ile Geometry sınıfına dönüştü.
     * Parametre nesnenin instanceof metodu ile sınıfı tes edildi ve ilgili fonksiyon çalıştırıldı.
    */
    public String write(Geometry geom) {
            if(geom instanceof Point){
                return writePoint((Point) geom, true);
            }else if(geom instanceof MultiPoint){
                return writeMultiPoint((MultiPoint) geom);
            }else if(geom instanceof LineString){
                return writeLineString((LineString) geom, true);
            }else if(geom instanceof MultiLineString){
                return writeMultiLineString((MultiLineString) geom);
            }else if(geom instanceof Polygon){
                return writePolygon((Polygon) geom, true);
            }else if(geom instanceof MultiPolygon){
                return writeMultiPolygon((MultiPolygon) geom);
            }else if(geom instanceof GeometryCollection){
                return writeGeometryCollection((GeometryCollection) geom);
            }
            return "Unsupported Geometry Type";

    }

    
    /* Point sınıfından bir nesneyi parametre olarak alır.
     * POINT'in WKT formatında string ifadesini döndürür.
     * tipBelirtec parametresi POINT ifadesinin dönen değerde olup olmayacağını belirler.
     * tipBelirtec=true -> POINT (10 20)
     * tipBelirtec=false -> (10 20)
    */
    public String writePoint(Point point, boolean tipBelirtec){
        StringBuffer pointWKT = new StringBuffer();
        if(point.isEmpty()){
            pointWKT.append(POINT+" "+EMPTY);
            return pointWKT.toString();
        }
        if(tipBelirtec){
            pointWKT.append(POINT+" ");
        }
        pointWKT.append(AC_PARANTEZ+point.getX()+" "+point.getY()+KAPAT_PARANTEZ);
        return pointWKT.toString();
    }

    
    /* MultiPoint sınıfından bir nesneyi parametre olarak alır.
     * MULTIPOINT'in WKT formatında string ifadesini döndürür.
     * MULTIPOINT WKT formatında iki tür yazım şekli vardır.
     * YENI_MULTIPOINT_YAZIM_TIPI ile yazım şekli değiştirilir.
     * YENI_MULTIPOINT_YAZIM_TIPI:
        * true -> MULTIPOINT ((10 40), (40 30), (20 20), (30 10)) çıktısını döndürür.
        * false -> MULTIPOINT (10 40, 40 30, 20 20, 30 10) çıktısını döndürür.
    */
    private static final boolean YENI_MULTIPOINT_YAZIM_TIPI = false;
    public String writeMultiPoint(MultiPoint multiPoint){
        StringBuffer multiPointWKT = new StringBuffer();
        if(multiPoint.isEmpty()){
            multiPointWKT.append(MULTIPOINT+" "+EMPTY);
            return multiPointWKT.toString();
        }

        multiPointWKT.append(MULTIPOINT+" "+AC_PARANTEZ);

        for(int i=0; i<multiPoint.size(); i++){
            if(YENI_MULTIPOINT_YAZIM_TIPI){
                multiPointWKT.append(AC_PARANTEZ);
            }
            multiPointWKT.append(multiPoint.get(i).getX()+" "+multiPoint.get(i).getY());
            if(YENI_MULTIPOINT_YAZIM_TIPI){
                multiPointWKT.append(KAPAT_PARANTEZ);
            }
            if(i != multiPoint.size()-1){
                multiPointWKT.append(VIRGUL);
            }
        }
        multiPointWKT.append(KAPAT_PARANTEZ);
        return multiPointWKT.toString();
    }        

    
    /* LineString sınıfından bir nesneyi parametre olarak alır.
     * LINESTRING'in WKT formatında string ifadesini döndürür.
     * tipBelirtec parametresi LINESTRING ifadesinin dönen değerde olup olmayacağını belirler.
     * tipBelirtec=true -> LINESTRING (30 10, 10 30, 40 40)
     * tipBelirtec=false -> (30 10, 10 30, 40 40)
    */
    public String writeLineString(LineString lineString, boolean tipBelirtec){
        StringBuffer lineStringWKT = new StringBuffer();
        if(lineString.isEmpty()){
            lineStringWKT.append(LINESTRING+" "+EMPTY);
            return lineStringWKT.toString();
        }

        if(tipBelirtec){
            lineStringWKT.append(LINESTRING+" ");
        }
        lineStringWKT.append(AC_PARANTEZ);
        for(int i=0; i<lineString.getNumCoords(); i++){
            lineStringWKT.append(lineString.getX(i)+" "+lineString.getY(i));
            if(i != lineString.getNumCoords()-1){
                lineStringWKT.append(VIRGUL+" ");
            }
        }
        lineStringWKT.append(KAPAT_PARANTEZ);
        return lineStringWKT.toString();
    }

    
    /* MultiLinesString sınıfından bir nesneyi parametre olarak alır.
     * MULTILINESTRING'in WKT formatında string ifadesini döndürür.
    */
    public String writeMultiLineString(MultiLineString multiLineString){
        StringBuffer multiLineStringWKT = new StringBuffer();
        if(multiLineString.isEmpty()){
            multiLineStringWKT.append(MULTILINESTRING+" "+EMPTY);
            return multiLineStringWKT.toString();
        }

        multiLineStringWKT.append(MULTILINESTRING+" "+AC_PARANTEZ);
        for(int i=0; i<multiLineString.size(); i++){
            multiLineStringWKT.append(writeLineString(multiLineString.get(i), false));
            if(i != multiLineString.size()-1){
                multiLineStringWKT.append(VIRGUL);
            }
        }
        multiLineStringWKT.append(KAPAT_PARANTEZ);
        return multiLineStringWKT.toString();
    }

    
    /* Polygon sınıfından bir nesneyi parametre olarak alır.
     * POLYGON'un WKT formatında string ifadesini döndürür.
     * tipBelirtec parametresi POLYGON ifadesinin dönen değerde olup olmayacağını belirler.
     * tipBelirtec=true -> POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))
     * tipBelirtec=false -> ((30 10, 40 40, 20 40, 10 20, 30 10))
    */
    public String writePolygon(Polygon polygon, boolean tipBelirtec){
        StringBuffer polygonWKT = new StringBuffer();
        if(polygon.isEmpty()){
            polygonWKT.append(POLYGON+" "+EMPTY);
            return polygonWKT.toString();
        }
        if(tipBelirtec){
            polygonWKT.append(POLYGON+" ");
        }
        polygonWKT.append(AC_PARANTEZ);
        polygonWKT.append(writeLineString(polygon.getOuter(), false));
        if(polygon.getNumHoles() > 0){
            polygonWKT.append(VIRGUL);
        }
        for(int i=0; i<polygon.getNumHoles(); i++){
            polygonWKT.append(writeLineString(polygon.getHole(i), false));
            if(i != polygon.getNumHoles()-1){
                polygonWKT.append(VIRGUL);
            }
        }
        polygonWKT.append(KAPAT_PARANTEZ);
        return polygonWKT.toString();
    }

    
    /* MultiPolygon sınıfından bir nesneyi parametre olarak alır.
     * MULTIPOLYGON'un WKT formatında string ifadesini döndürür.
    */
    public String writeMultiPolygon(MultiPolygon multiPolygon){
        StringBuffer multiPolygonWKT = new StringBuffer();
        if(multiPolygon.isEmpty()){
            multiPolygonWKT.append(MULTIPOLYGON+" "+EMPTY);
            return multiPolygonWKT.toString();
        }

        multiPolygonWKT.append(MULTIPOLYGON+" "+AC_PARANTEZ);
        for(int i=0; i<multiPolygon.size(); i++){
            multiPolygonWKT.append(writePolygon(multiPolygon.get(i), false));
            if(i != multiPolygon.size()-1){
                multiPolygonWKT.append(VIRGUL);
            }
        }
        multiPolygonWKT.append(KAPAT_PARANTEZ);
        return multiPolygonWKT.toString();
    }

    
    /* GeometryCollection sınıfından bir nesneyi parametre olarak alır.
     * GEOMETRYCOLLECTION'un WKT formatında string ifadesini döndürür.
    */
    public String writeGeometryCollection(GeometryCollection geometryCollection){
        StringBuffer geometryCollectionWKT = new StringBuffer();
        if(geometryCollection.isEmpty()){
            geometryCollectionWKT.append(GEOMETRYCOLLECTION+" "+EMPTY);
            return geometryCollectionWKT.toString();
        }

        geometryCollectionWKT.append(GEOMETRYCOLLECTION+" "+AC_PARANTEZ);            
        for(int i=0; i<geometryCollection.size(); i++){
            geometryCollectionWKT.append(write(geometryCollection.get(i)));
            if(i != geometryCollection.size()-1){
                geometryCollectionWKT.append(VIRGUL);
            }
        }
        geometryCollectionWKT.append(KAPAT_PARANTEZ);
        return geometryCollectionWKT.toString();
    }

}
