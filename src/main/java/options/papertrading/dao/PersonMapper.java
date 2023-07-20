//package options.papertrading.dao;
//
//import options.papertrading.models.users.Person;
//import org.springframework.jdbc.core.RowMapper;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class PersonMapper implements RowMapper<Person> {
//
//    @Override
//    public Person mapRow(ResultSet rs, int i) throws SQLException {
//        return new Person(rs.getInt("id"),
//                rs.getString("name"),
//                rs.getInt("age"),
//                rs.getString("email"));
//    }
//}
