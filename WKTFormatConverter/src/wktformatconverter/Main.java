package javaapplication2;

import com.sinergise.geometry.Geometry;
import java.text.ParseException;

/**
 *
 * @author BerkanKoca
 */
public class Main {

    public static void main(String[] args) throws ParseException {
        
        WKTWriter wKTWriter = new WKTWriter();       
        WKTReader wKTReader = new WKTReader();
        
        Geometry geometry =  new Geometry() {
            @Override
            public boolean isEmpty() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
                
        geometry = wKTReader.read("POINT (30 10)");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("MULTIPOINT (10 40, 40 30, 20 20, 30 10)");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("LINESTRING (30 10, 10 30, 40 40)");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("MULTILINESTRING ((10 10, 20 20, 10 40),(40 40, 30 30, 40 20, 30 10))");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("POLYGON ((35 10, 45 45, 15 40, 10 20, 35 10),(20 30, 35 35, 30 20, 20 30))");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("MULTIPOLYGON (((40 40, 20 45, 45 30, 40 40)),((20 35, 10 30, 10 10, 30 5, 45 20, 20 35),(30 20, 20 15, 20 25, 30 20)))");
        System.out.println("Writer:"+wKTWriter.write(geometry));
        
        geometry = wKTReader.read("GEOMETRYCOLLECTION(POINT(4 6),LINESTRING(4 6,7 10))");
        System.out.println("Writer:"+wKTWriter.write(geometry));
    }
    
}
