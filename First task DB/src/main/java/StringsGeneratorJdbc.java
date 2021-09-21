import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Stream;

public class StringsGeneratorJdbc {

    //language=SQL
    private static final String SQL_INSERT = "insert into food(name, production_date, expiration_date) values(?, to_timestamp(1284908050+random()*347155200), ?)";

    public JdbcTemplate jdbcTemplate;

    public StringsGeneratorJdbc(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void generate(String file){
        List<Map.Entry<String, String>> list = new ArrayList<>();

        Gson gson = new Gson();
        JsonObject data = gson.fromJson(readFile(file), JsonObject.class);
        JsonArray array = data.getAsJsonArray("food");

        for (JsonElement e: array){
            Map.Entry<String, String> entry = new AbstractMap.SimpleEntry<>(e.getAsJsonObject().get("name").getAsString(), e.getAsJsonObject().get("expiration_date").getAsString());
            list.add(entry);
        }

        for(int i = 0; i < 20000000; i++){
            Map.Entry<String, String> entry = list.get(new Random().nextInt(list.size()));
            insert(entry.getKey(), entry.getValue());
        }
    }

    private String readFile(String file){
        StringBuilder sb = new StringBuilder();

        try {
            Stream<String> stream = Files.lines(new File(file).toPath());
            stream.forEach(x -> sb.append(x));
        }catch(IOException e){
            throw new IllegalArgumentException("This file doesn't exist");
        }

        return sb.toString();
    }

    private void insert(String name, String expirationDate){
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(SQL_INSERT, new String[]{"id"});
            statement.setString(1, name);
            statement.setString(2, expirationDate);
            return statement;
        }, keyHolder);
    }
}
