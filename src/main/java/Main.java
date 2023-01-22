import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, ParseException {

        // ЗАДАЧА 1. КОНВЕРТАЦИЯ ИЗ CSV В JSON

        String fileName = "data.csv";
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        // Получим список сотрудников из файла csv
        List<Employee> list = parseCSV(columnMapping, fileName);
        // Конвертируем список в формат json
        String json = listToJson(list);
        // Записываем строку формата json в файл
        writeString(json, "dataFromCsv.json");

        // ЗАДАЧА 2. КОНВЕРТАЦИЯ ИЗ XML В JSON

        // Получим список сотрудников из файла csv
        list = parseXML("data.xml");
        //parseXML("data.xml");
        // Конвертируем список в формат json
        json = listToJson(list);
        // Записываем строку формата json в файл
        writeString(json, "dataFromXml.json");

        //ЗАДАЧА 3. JSON ПАРСЕР

        //json = readString("dataFromCsv.json");              // так не работает
        //json = readString("dataFromPresentation.json");     // так работает
        String json_ = "[{\"id\":1,\"firstName\":\"David\",\"lastName\":\"Miller\",\"country\":\"Australia\",\"age\":30},{\"id\":2,\"firstName\":\"Ivan\",\"lastName\":\"Petrov\",\"country\":\"RU\",\"age\":23}]";
        list = jsonToList(json_);      // не удается досюда дойти, поэтому метод построен на строке, взятой из json файла
    }

    // Чтение из json в list
    private static List<Employee> jsonToList(String jsonText) throws ParseException {


        Employee employee;
        List<Employee> list = new ArrayList<Employee>();

        JSONParser parser = new JSONParser(); //1
        Object obj = parser.parse(jsonText);    //2
        JSONArray jsonArray = (JSONArray) obj;  //3

        //Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        //String json = gson.toJson(list, listType);

        for (int i = 0; i < jsonArray.size(); i++) {
            employee = gson.fromJson(jsonArray.get(i).toString(), Employee.class);
            list.add(employee);
            // вывод элементов списка в консоль
            System.out.println(employee.toString());
        }

        return list;
    }

    // Чтение из файла json
    private static String readString(String fileName) {
        JSONParser parser = new JSONParser();
        //JSONObject jsonObject = null;
        try {
            Object obj = parser.parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) obj;
            return jsonObject.toJSONString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Получение списка из файла csv
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    // Получение списка из файла XML
    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();
        List<Employee> list = new ArrayList<Employee>();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (node_.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node_;
                list.add(new Employee(
                        Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()),
                        element.getElementsByTagName("firstName").item(0).getTextContent(),
                        element.getElementsByTagName("lastName").item(0).getTextContent(),
                        element.getElementsByTagName("country").item(0).getTextContent(),
                        Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())
                ));
            }
        }
        return list;
    }

    // Конвертация списка в json
    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    // Запись json в файл
    public static void writeString(String list, String fileName) {

        try (FileWriter file = new
                FileWriter(fileName)) {
            file.write(list);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}//end main
