package com.xuexiang.xupdateservice.utils;

import io.jsonwebtoken.Claims;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TokenUtilsTest {

    @Test
    public void createJwtTokenAndParseJwt_roundTripsUserId() {
        String loginName = "admin";

        String token = TokenUtils.createJwtToken(loginName);
        Claims claims = TokenUtils.parseJWT(token);

        assertNotNull(token);
        assertEquals(loginName, claims.getId());
        assertEquals("www.github.com", claims.getIssuer());
        assertEquals("xuexiangjys@163.com", claims.getSubject());
    }

    @Test
    public void createJwtTokenAndParseJwt_workOnMainRuntimeClasspath() throws Exception {
        String loginName = "admin";
        Process process = new ProcessBuilder(buildCommand(loginName)).start();

        int exitCode = process.waitFor();
        String output = readAll(process.getInputStream());
        String error = readAll(process.getErrorStream());

        assertEquals("Subprocess stderr: " + error, 0, exitCode);
        assertTrue("Expected login name in subprocess output but got: " + output, output.contains(loginName));
    }

    private List<String> buildCommand(String loginName) {
        String javaBin = System.getProperty("java.home") + "/bin/java";
        String classpath = System.getProperty("testClassesDirs")
                + System.getProperty("path.separator")
                + System.getProperty("mainRuntimeClasspath");

        List<String> command = new ArrayList<>();
        command.add(javaBin);
        command.add("-cp");
        command.add(classpath);
        command.add(TokenUtilsCli.class.getName());
        command.add(loginName);
        return command;
    }

    private String readAll(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
        return outputStream.toString(StandardCharsets.UTF_8.name());
    }
}
