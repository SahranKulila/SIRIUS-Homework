package edu.ezip.ing1.pds.business.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Student;
import edu.ezip.ing1.pds.business.dto.Students;
import edu.ezip.ing1.pds.commons.Request;
import edu.ezip.ing1.pds.commons.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMartCityService {

    private final static String LoggingLabel = "B u s i n e s s - S e r v e r";
    private final Logger logger = LoggerFactory.getLogger(LoggingLabel);

    private enum Queries {
        SELECT_ALL_STUDENTS("SELECT t.name, t.firstname, t.groupname FROM students t"),
        INSERT_STUDENT("INSERT into students (name, firstname, groupname) values (?, ?, ?)"),
        SELECT_STUDENTS("SELECT t.name, t.firstname, t.groupname FROM students t");
        private final String query;

        private Queries(final String query) {
            this.query = query;
        }
    }

    public static XMartCityService inst = null;
    public static final XMartCityService getInstance()  {
        if(inst == null) {
            inst = new XMartCityService();
        }
        return inst;
    }

    private XMartCityService() {

    }

    public final Response dispatch(final Request request, final Connection connection)
            throws InvocationTargetException, IllegalAccessException, SQLException, IOException {
        Response response = null;

        final Queries queryEnum;
        //
        try {
            queryEnum = Enum.valueOf(Queries.class, request.getRequestOrder());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid query type: {}", request.getRequestOrder(), e);
            throw new IllegalArgumentException("Unsupported query type provided: " + request.getRequestOrder());
        }
        //
        switch(queryEnum) {
            case SELECT_ALL_STUDENTS:
                response = SelectAllStudents(request, connection);
                break;
            case INSERT_STUDENT:
                response = InsertStudent(request, connection);
                break;
            default:
                break;
        }

        return response;
    }

    private Response InsertStudent(final Request request, final Connection connection) throws SQLException, IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        if (request.getRequestBody() == null || request.getRequestBody().isEmpty()) {
            throw new IOException("Request body is missing for InsertStudent");
        }

        final Student student = objectMapper.readValue(request.getRequestBody(), Student.class);
        logger.info("Insertion d'un étudiant reçu : {}", student);

        final PreparedStatement stmt = connection.prepareStatement(Queries.INSERT_STUDENT.query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, student.getName());
        stmt.setString(2, student.getFirstname());
        stmt.setString(3, student.getGroup());

        int rowsAffected = stmt.executeUpdate();

        if (rowsAffected == 0) {
            logger.warn("Aucun étudiant inséré.");
            return new Response(request.getRequestId(), "Erreur: Aucune insertion effectuée.");
        }

        final ResultSet res = stmt.getGeneratedKeys();

        if (!res.next()) {
            logger.error("No generated student ID returned from database");
            return new Response(request.getRequestId(), "Erreur: Aucun ID généré.");
        }

        Map<String, Integer> responseMap = new HashMap<>();
        responseMap.put("student_id", res.getInt(1));

        logger.info("Étudiant inséré avec succès. ID généré: {}", res.getInt(1));

        return new Response(request.getRequestId(), objectMapper.writeValueAsString(responseMap));
    }


    private Response SelectAllStudents(final Request request, final Connection connection) throws SQLException, JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final Statement stmt = connection.createStatement();
        final ResultSet res = stmt.executeQuery(Queries.SELECT_ALL_STUDENTS.query);
        Students students = new Students();
        while (res.next()) {
            Student student = new Student();
            student.setName(res.getString(1));
            student.setFirstname(res.getString(2));
            student.setGroup(res.getString(3));
            students.add(student);
        }
        return new Response(request.getRequestId(), objectMapper.writeValueAsString(students));
    }

}
