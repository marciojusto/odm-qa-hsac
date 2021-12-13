package com.teamwill.leaseforge.qa.slim;

import com.github.javafaker.Faker;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class RandomDataProvider {
    private final Faker faker = new Faker(Locale.ENGLISH);
    private final Random rand = new Random(new Date().getTime());

    public String email() {
        //UUID.randomUUID().toString()+
        return UUID.randomUUID().toString().concat(faker.internet()
                .emailAddress());
    }

    public String password() {
        return faker.regexify("[a-z1-9]{10}");
    }

    public String phoneNumber() {
        // return faker.phoneNumber()
        //            .phoneNumber();
        String phone=faker.phoneNumber().phoneNumber();
        if (phone.length() <= 20 ) {
            return phone;
        }
        else {
            return phone.substring(0, 20);
        }
    }


    public String city() {
        return faker.address()
                    .city();
    }

    public String country() {
        return faker.address()
                    .country();
    }

    public String state() {
        return faker.address()
                    .state();
    }

    public String streetAddress() {
        return faker.address()
                    .streetAddress();
    }

    public String zipCode() {
        return faker.address()
                    .zipCode();
    }

    public String fullName() {
        return faker.name()
                    .fullName();
    }

    public String firstName() {
        return faker.name()
                    .firstName();
    }

    public String lastName() {
        return faker.name()
                    .lastName();
    }

    public String username() {
        return UUID.randomUUID().toString().concat(faker.name()
                    .username());
    }
    public String resource() {
//        return faker.team()
//                    .state();
        return UUID.randomUUID().toString();
    }
    public String projectName() {
//        return faker.princessBride()
//                    .character();
        return UUID.randomUUID().toString();
    }
    public String integer(int low, int high) {
        return "" + (rand.nextInt(high - low) + low);
    }

    public String num(int low, int high) {
        return "" + ((rand.nextDouble() * (high - low)) + low);
    }
}
