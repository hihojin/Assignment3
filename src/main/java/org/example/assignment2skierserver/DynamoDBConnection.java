package org.example.assignment2skierserver;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDBConnection {
    public DynamoDBConnection(int skierID, int resortID, int seasonID, int dayID, int time, int liftID) {
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        String tableName = "SkiersDB";
        String key = "skierID";
        int keyVal = skierID;
        String resortIDKey = "resortID";
        int resortIDValue = resortID;
        String seasonIDKey = "seasonID";
        int seasonIDValue = seasonID;
        String dayIDKey = "dayID";
        int dayIDValue = dayID;
        String timeKey = "time";
        int timeValue = time;
        String liftIDKey = "liftID";
        int liftIDValue = liftID;

        putItemInTable(ddb,
                tableName,
                key,
                keyVal,
                resortIDKey,
                resortIDValue,
                seasonIDKey,
                seasonIDValue,
                dayIDKey,
                dayIDValue,
                timeKey,
                timeValue,
                liftIDKey,
                liftIDValue);
        ddb.close();
    }

    public static void putItemInTable(DynamoDbClient ddb,
                                      String tableName,
                                      String key,
                                      int keyVal,
                                      String resortIDKey,
                                      int resortIDValue,
                                      String seasonIDKey,
                                      int seasonIDValue,
                                      String dayIDKey,
                                      int dayIDValue,
                                      String timeKey,
                                      int timeValue,
                                      String liftIDKey,
                                      int liftIDValue) {

        Map<String, AttributeValue> itemValues = new HashMap<>();
        // key is skierID (primary)
        itemValues.put(key, AttributeValue.builder().n(String.valueOf(keyVal)).build());

        itemValues.put(resortIDKey, AttributeValue.builder().n(String.valueOf(resortIDValue)).build());

        itemValues.put(seasonIDKey, AttributeValue.builder().n(String.valueOf(seasonIDValue)).build());

        itemValues.put(dayIDKey, AttributeValue.builder().n(String.valueOf(dayIDValue)).build());

        itemValues.put(timeKey, AttributeValue.builder().n(String.valueOf(timeValue)).build());

        itemValues.put(liftIDKey, AttributeValue.builder().n(String.valueOf(liftIDValue)).build());


        PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            PutItemResponse response = ddb.putItem(putItemRequest);
//            System.out.println(tableName + " was successfully updated. The request id is "
//                    + response.responseMetadata().requestId());
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", "SkiersDB");
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // drop table for testing
//    public static void main(String args[]) {
//        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
//                .region(Region.US_WEST_2)
//                .build();
//        DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder()
//                .tableName("SkiersDB")
//                .build();
//
//        dynamoDbClient.deleteTable(deleteTableRequest);
//
//        dynamoDbClient.close();
//    }
}
