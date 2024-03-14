package org.example.assignment2skierserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class SkierServlet extends HttpServlet {
    private RMQChannelPool pool;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            int CHANNELS = 100; // channel size
            String HOST = "35.91.222.214";  // change this ec2 start
            String USERNAME = "admin";
            String PASSWORD = "password";
            String VIRTUALHOST = "/";
            int PORT = 5672;
            pool = new RMQChannelPool(CHANNELS, HOST, PORT, USERNAME, PASSWORD, VIRTUALHOST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // testing purpose for now
        res.setContentType("text/plain");
        String urlPath = req.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            res.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");
        // and now validate url path and return the response status code
        // (and maybe also some value if input is valid)

//        if (!isUrlValid(urlParts)) {
//            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
//        } else {
            res.setStatus(HttpServletResponse.SC_OK);
            // do any sophisticated processing with urlParts which contains all the url params
            // TODO: process url params in `urlParts`
            res.getWriter().write("get homepage!");
        //}
    }

    private static boolean isParsable(String input) {
        // Helper method to check if a string can be parsed to an integer
        try {
            // Attempt to parse the string
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            // Catch the exception if parsing fails
            return false;
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        String urlPath = request.getPathInfo();
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }

        String[] urlParts = urlPath.split("/");

        final int urllLength = 8;
        if (urlParts.length != urllLength) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("missing paramterers");
            return;
        }
        // example: [, 12, seasons, 2019, day, 1, skier, 123]
        int resortID = Integer.parseInt(urlParts[1]);
        int seasonID = Integer.parseInt(urlParts[3]);
        int dayID = Integer.parseInt(urlParts[5]);
        int skierID = Integer.parseInt(urlParts[7]);

        if (!isParsable(urlParts[1])) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("resortID should be int type");
            return;
        }
        if (resortID < 1 || resortID > 10) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("resortID should be between 1 and 10");
            return;
        }
        if (!urlParts[2].equals("seasons")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("should be 'seasons' here");
            return;
        }
        if (!isParsable(urlParts[3]) || seasonID != 2024) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("seasonID should be year 2024");
            return;
        }
        if (Integer.parseInt(urlParts[3]) < 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("seasonID should be non-negative");
            return;
        }
        if (!urlParts[4].equals("days")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("should be 'days' here");
            return;
        }
        if (!isParsable(urlParts[5]) || dayID != 1) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("dayID should be 1");
            return;
        }
//        if (Integer.parseInt(urlParts[5]) < 1 || Integer.parseInt(urlParts[5]) > 366) {
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.getWriter().write("min dayID is 1 and max dayID is 366");
//            return;
//        }
        if (!urlParts[6].equals("skiers")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("should be 'skiers' here");
            return;
        }
        if (!isParsable(urlParts[7])) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("skierID should be int type");
            return;
        }
        if (skierID < 1 || skierID > 100000) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("skierID should be between 1 and 100000");
            return;
        }

        // Read from request & request body validation
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append('\n');
        }
        String data = buffer.toString();

        JsonObject jsonObject = JsonParser.parseString(data)
                .getAsJsonObject();

        int time;
        int liftID;
        try {
            time = jsonObject.get("time").getAsInt();
            liftID = jsonObject.get("liftID").getAsInt();

            if ( time < 0 || liftID < 0 ) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("time and liftID should be greater than" +
                        " or equal to 0");
                return;
            }

            if (time < 1 || time > 360) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("time should be between 1 and 360");
                return;
            }
            if (liftID < 1 || liftID > 40) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("liftID should be between 1 and 40");
                return;
            }

        } catch (Exception e) {
            response.getWriter().write("time and liftID need to be integer" +
                    " and filled");
            return;
        }

        JsonObject formattedData = new JsonObject();
        formattedData.addProperty("time", time);
        formattedData.addProperty("liftID", liftID);

        formattedData.addProperty("resortID", resortID);
        formattedData.addProperty("seasonID", seasonID);
        formattedData.addProperty("dayID", dayID);
        formattedData.addProperty("skierID", skierID);

        try {
            Channel chanel = pool.borrowObject();
            String queueName = "skiersQueue";
            chanel.queueDeclare(queueName, false, false, false, null);
            byte[] payLoad = formattedData.toString().getBytes();
            chanel.basicPublish("", queueName, null, payLoad);
            pool.returnObject(chanel);

        } catch (Exception e) {
            System.out.println("not sent");
            throw new RuntimeException(e);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
