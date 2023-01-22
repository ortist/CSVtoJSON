import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        // Создаем файл csv
        String fileName = "data.csv"; // имя файла
        // Создаем записи
        String[] employee01 = "1,David,Miller,Australia,30".split(",");
        String[] employee02 = "2,Ivan,Petrov,RU,23".split(",");
        // Создаем экземпляр CSVWriter
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            // Записываем запись в файл csv
            writer.writeNext(employee01);
            writer.writeNext(employee02);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        // Получим список сотрудников из файла csv
        List<Employee> list = parseCSV(columnMapping, fileName);
        // Конвертируем список в формат json
        String json = listToJson(list);
        // Записываем строку формата json в файл
        writeString(json);
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

    // Конвертация списка в json
    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    // Запись json в файл
    public static void writeString(String list) {

        try (FileWriter file = new
                FileWriter("data.json")) {
            file.write(list);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}//end main
