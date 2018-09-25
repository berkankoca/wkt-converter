package javaapplication2;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;


public class WKTReader
{
    // WKT formatında olabilecek ortak string değerler.
    private static final String EMPTY = "EMPTY";
    private static final String VIRGUL = ",";
    private static final String AC_PARATEZ = "(";
    private static final String KAPAT_PARANTEZ = ")";

    /*
     * WKT formatında gelen string değeri, istenilen kriterlere göre parçalamak için
        * StreamTokonizer kullanıldı.
    */
    private StreamTokenizer tokenizer;

    //StringReader tanımlandı.
    public  Geometry read(String wktString) throws ParseException {
        StringReader stringReader = new StringReader(wktString);
        try {
            return read(stringReader);
        }
        finally {
            stringReader.close();
        }
    }

    //StreamTokonizer özellikleri tanımlandı.
    public  Geometry read(Reader reader) throws ParseException {
        tokenizer = new StreamTokenizer(reader);

        tokenizer.resetSyntax();
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('0', '9');
        tokenizer.whitespaceChars(0, ' ');

        try {
            return readGeometryTagged();
        }
        catch (IOException e) {
            return null;
        }
    }

    
    /*
     * readGeometryTagged ilgili nesne oluşturma fonksiyonlarını yönetir.
     * Bir sonraki token değerini saptayarak, WKT formtının temsil ettiği geometrik şekli oluşturan fonksiyonu çalıştırır.
        * Örneğin bir sonraki token değeri POINT ise readPoint() fonksiyonunu çalıştırır.
    */
    private  Geometry readGeometryTagged() throws IOException, ParseException {
        String type = null;
        
        try{
            type = getNextWord();
        }catch(IOException e){
            return null;
        }catch(ParseException e){
            return null;
        }

        if (type.equalsIgnoreCase("POINT")) {
            return readPoint();
        }else if (type.equalsIgnoreCase("MULTIPOINT")) {
            return readMultiPoint();
        }else if (type.equalsIgnoreCase("LINESTRING")) {
            return readLineString();
        }else if (type.equalsIgnoreCase("MULTILINESTRING")) {
            return readMultiLineString();
        }else if (type.equalsIgnoreCase("POLYGON")) {
            return readPolygon();
        }else if (type.equalsIgnoreCase("MULTIPOLYGON")) {
            return readMultiPolygon();
        }else if (type.equalsIgnoreCase("GEOMETRYCOLLECTION")) {
            return readGeometryCollection();
        }
        return null;
    }

    
    /*
     * readPoint fonksiyonu, WKT formatındaki değere göre Point sınıfından nesne oluşturur ve döndürür.
    */
    private  Point readPoint() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new Point();
        }
        Point point = new Point(getNextNumber(), getNextNumber());
        getNextCloser();
        return point;
    }

    
    /*
     * readMultiPoint fonksiyonu, WKT formatındaki değere göre MultiPoint sınıfından nesne oluşturur ve döndürür.
     * ESKI_MULTIPOINT_YAZIM_TIPI ile kullanılan WKT formatının türü belirlenir:
        * false -> MULTIPOINT ((10 40), (40 30), (20 20), (30 10)) WKT formatı kullanılır.
        * true -> MULTIPOINT (10 40, 40 30, 20 20, 30 10) WKT formatı kullanılır. 
            * Değer true ise her iki WKT formatı desteklenmektedir.
    */
    private static final boolean ESKI_MULTIPOINT_YAZIM_TIPI = true;
    private  MultiPoint readMultiPoint() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new MultiPoint();
        }

        if (ESKI_MULTIPOINT_YAZIM_TIPI) {
                String nextWord = lookaheadWord();
                if (nextWord != AC_PARATEZ) {
                    ArrayList<Point> listPoint = new ArrayList<>();

                    listPoint.add(new Point(getNextNumber(), getNextNumber()));
                    nextToken = getNextCloserOrComma();
                    while(nextToken.equals(VIRGUL)){
                        listPoint.add(new Point(getNextNumber(), getNextNumber()));
                        nextToken = getNextCloserOrComma();
                    }
                    Point[] points = new Point[listPoint.size()];
                    for(int i=0; i<points.length; i++){
                        points[i] = listPoint.get(i);
                    }

                    return new MultiPoint(points);
                }
        }

        ArrayList points = new ArrayList();
        Point point = readPoint();
        points.add(point);
        nextToken = getNextCloserOrComma();
        while (nextToken.equals(VIRGUL)) {
            point = readPoint();
            points.add(point);
            nextToken = getNextCloserOrComma();
        }
        
        Point[] array = new Point[points.size()];
        for(int i=0; i<array.length; i++){
            array[i] = (Point) points.get(i);
        }
        return new MultiPoint(array);
    }

    
    /*
     * readLineString fonksiyonu, WKT formatındaki değere göre LineString sınıfından nesne oluşturur ve döndürür.
    */
    private  LineString readLineString() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new LineString();
        }
        
        ArrayList<Double> listLineString = new ArrayList<>();
        listLineString.add(getNextNumber());
        listLineString.add(getNextNumber());
        nextToken = getNextCloserOrComma();
        while (nextToken.equals(VIRGUL)) {
            listLineString.add(getNextNumber());
            listLineString.add(getNextNumber());
            nextToken = getNextCloserOrComma();
        }

        double[] arrayLineString = new double[listLineString.size()];
        for(int i=0; i<arrayLineString.length; i++){
            arrayLineString[i] = (double) listLineString.get(i);
        }
        
        return new LineString(arrayLineString);
    }
    
    
    /*
     * readMultiLineString fonksiyonu, WKT formatındaki değere göre MultiLineString sınıfından nesne oluşturur ve döndürür.
    */
    private  MultiLineString readMultiLineString() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new MultiLineString();
        }
        
        ArrayList<LineString> listLineString = new ArrayList();
        listLineString.add(readLineString());
        nextToken = getNextCloserOrComma();
        while (nextToken.equals(VIRGUL)) {
            listLineString.add(readLineString());
            nextToken = getNextCloserOrComma();
        }

        LineString[] arrayLineString = new LineString[listLineString.size()];
        for(int i=0; i<arrayLineString.length; i++){
            arrayLineString[i] = (LineString) listLineString.get(i);
        }

        return new MultiLineString(arrayLineString);
    }
    
    
    /*
     * readPolygon fonksiyonu, WKT formatındaki değere göre Polygon sınıfından nesne oluşturur ve döndürür.
    */
    private Polygon readPolygon() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new Polygon();
        }
        
        ArrayList<LineString> listHoles = new ArrayList<>();
        LineString outer = readLineString();
        nextToken = getNextCloserOrComma();
        while (nextToken.equals(VIRGUL)) {
            listHoles.add(readLineString());
            nextToken = getNextCloserOrComma();
        }

        LineString[] arrayHoles = new LineString[listHoles.size()];
        for(int i=0; i<arrayHoles.length; i++){
            arrayHoles[i] = (LineString) listHoles.get(i);
        }

        return new Polygon(outer, arrayHoles);
    }

    
    /*
     * readMultiPolygon fonksiyonu, WKT formatındaki değere göre MultiPolygon sınıfından nesne oluşturur ve döndürür.
    */
    private MultiPolygon readMultiPolygon() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new MultiPolygon();
        }
        
        ArrayList<Polygon> listPolygon = new ArrayList<>();
        listPolygon.add(readPolygon());
        nextToken = getNextCloserOrComma();
        while (nextToken.equals(VIRGUL)) {
            listPolygon.add(readPolygon());
            nextToken = getNextCloserOrComma();
        }

        Polygon[] arrayPolygon = new Polygon[listPolygon.size()];
        for(int i=0; i<arrayPolygon.length; i++){
            arrayPolygon[i] = (Polygon) listPolygon.get(i);
        }

        return new MultiPolygon(arrayPolygon);
    }

    
    /*
     * readGeometryCollection fonksiyonu, WKT formatındaki değere göre GeometryCollection sınıfından nesne oluşturur ve döndürür.
    */
    private GeometryCollection  readGeometryCollection() throws IOException, ParseException {
        String nextToken = getNextEmptyOrOpener();
        if (nextToken.equals(EMPTY)) {
            return new GeometryCollection();
        }
        
        ArrayList<Geometry> listGeometryCollection = new ArrayList<>();
        listGeometryCollection.add(readGeometryTagged());
        nextToken = getNextCloserOrComma();
        while (nextToken.equals(VIRGUL)) {
            listGeometryCollection.add(readGeometryTagged());
            nextToken = getNextCloserOrComma();
        }

        Geometry[] arrayGeometry = new Geometry[listGeometryCollection.size()];
        for(int i=0; i<arrayGeometry.length; i++){
            arrayGeometry[i] = (Geometry) listGeometryCollection.get(i);
        }

        return new GeometryCollection(arrayGeometry);
    }    
    
    
    //Bir sonraki token değerini double tipinde döndürür.
    private  double getNextNumber() throws IOException, ParseException {
        int type = tokenizer.nextToken();
        switch (type) {
            case StreamTokenizer.TT_WORD:
            {
                if (tokenizer.sval.equalsIgnoreCase("NaN")) {
                    return Double.NaN;
                }
                else {
                    try {
                        return Double.parseDouble(tokenizer.sval);
                    }
                    catch (NumberFormatException ex) {
                    }
                }
            }
        }
        return 0.0;
    }

    /*
     * Bir sonraki değerin boşluk yada parantez açma karakteri olduğunu saptar.
        * Bir sonraki karakter boşluk yada parantez açma ise, karakterin değerini döndürür.
    */
    private  String getNextEmptyOrOpener() throws IOException, ParseException {
        String nextWord = getNextWord();
        if (nextWord.equals(EMPTY) || nextWord.equals(AC_PARATEZ)) {
          return nextWord;
        }
        return null;
    }

    /*
     * getNextCloserOrComma fonksiyonu bir sonraki değerin parantez kapatma yada virgül karakteri olduğunu saptar.
        * Bir sonraki karakter parantez kapatma yada virgül ise, karakterin değerini döndürür.
    */
    private  String getNextCloserOrComma() throws IOException, ParseException {
        String nextWord = getNextWord();
        if (nextWord.equals(VIRGUL) || nextWord.equals(KAPAT_PARANTEZ)) {
          return nextWord;
        }
        return null;
    }

    /*
     * getNextCloser fonksiyonu bir sonraki parantez kapatma karakterini saptar.
        * Bir sonraki karakter parantez kapatma ise, karakterin değerini döndürür.
    */
    private  String getNextCloser() throws IOException, ParseException {
        String nextWord = getNextWord();
        if (nextWord.equals(KAPAT_PARANTEZ)) {
          return nextWord;
        }
        return null;
    }

    /*
     * getNextWord fonksiyonu bir sonraki token değerini saptar.
        * Bir sonraki token tipi kelime ise kelimenin sitring değerini döner.
        * Bir sonraki token başka bir karakter ise (parantez açma, parantez kapatma, virgül),
            token değerini döndürür.
    */
    private  String getNextWord() throws IOException, ParseException {
        int type = tokenizer.nextToken();
        switch (type) {
            case StreamTokenizer.TT_WORD:
                String word = tokenizer.sval;
                if (word.equalsIgnoreCase(EMPTY))
                  return EMPTY;
                return word;
            case '(': return AC_PARATEZ;
            case ')': return KAPAT_PARANTEZ;
            case ',': return VIRGUL;
        }
        return null;
    }

    // Bir sonraki token değerini, ttype değişikliği yapmadan döndürür.
    private  String lookaheadWord() throws IOException, ParseException {
        String nextWord = getNextWord();
        tokenizer.pushBack();
        return nextWord;
    }

    //tokenString fonksiyonu, tokonizerin ttype değerine göre string ifade döndürür.
    private  String tokenString(){
        switch (tokenizer.ttype) {
            case StreamTokenizer.TT_NUMBER:
                return "<NUMBER>";
            case StreamTokenizer.TT_EOL:
                return "End-of-Line";
            case StreamTokenizer.TT_EOF: 
                return "End-of-Stream";
            case StreamTokenizer.TT_WORD: 
                return "'" + tokenizer.sval + "'";
        }
        return "'" + (char) tokenizer.ttype + "'";
    }

}

