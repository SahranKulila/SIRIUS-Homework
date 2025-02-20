package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Student;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;
import java.util.Map;

public class InsertStudentsClientRequest extends ClientRequest<Student, String> {

    public InsertStudentsClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Student info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public String readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(body);

        if (rootNode.has("response")) {
            rootNode = rootNode.get("response");
        }

        JsonNode responseBodyNode = rootNode.get("response_body");

        final Map<String, Integer> studentIdMap = mapper.treeToValue(responseBodyNode, Map.class);

        return studentIdMap.get("student_id").toString();
    }

}
