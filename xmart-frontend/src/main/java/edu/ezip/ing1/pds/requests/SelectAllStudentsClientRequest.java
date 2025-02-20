package edu.ezip.ing1.pds.requests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ezip.ing1.pds.business.dto.Students;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

import java.io.IOException;

public class SelectAllStudentsClientRequest extends ClientRequest<Object, Students> {

    public SelectAllStudentsClientRequest(
            NetworkConfig networkConfig, int myBirthDate, Request request, Object info, byte[] bytes)
            throws IOException {
        super(networkConfig, myBirthDate, request, info, bytes);
    }

    @Override
    public Students readResult(String body) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = mapper.readTree(body);

        if (rootNode.has("response")) {
            rootNode = rootNode.get("response");
        }

        JsonNode responseBodyNode = rootNode.get("response_body");

        return mapper.treeToValue(responseBodyNode, Students.class);
    }

}
