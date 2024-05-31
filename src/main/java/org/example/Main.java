package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1)
            throw new RuntimeException("Введите путь до файла");

        Gson gson = new Gson();
        String content;
        try {
            content = Files.readString(Path.of(args[0]), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Файл не найден");
        }
        Tickets tickets;
        try {
            tickets = gson.fromJson(content, Tickets.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения файла");
        }

        minTimeCalc(tickets, "VVO", "TLV");
        difAvgMedian(tickets, "VVO", "TLV");
    }

    public static void minTimeCalc(Tickets tickets, String origin, String destination) {
        Map<String, Long> timeMap = new HashMap<>();
        for (Ticket ticket : tickets.tickets) {
            if (!ticket.origin.equals(origin) || !ticket.destination.equals(destination))
                continue;

            long time = Duration.between(dateTimeFormat(ticket.departure_date, ticket.departure_time),
                    dateTimeFormat(ticket.arrival_date, ticket.arrival_time)).toMinutes();

            if (timeMap.containsKey(ticket.carrier) && timeMap.get(ticket.carrier).doubleValue() > time)
                timeMap.put(ticket.carrier, time);
            else timeMap.put(ticket.carrier, time);
        }

        if (!timeMap.isEmpty()) {
            System.out.println("Минимальное время полета между городами для каждого авиаперевозчика:");
            for (Map.Entry<String, Long> entry : timeMap.entrySet()) {
                int hours = (int) (entry.getValue() / 60);
                int minutes = (int) (entry.getValue() % 60);
                System.out.println(entry.getKey() + ": " + hours + ":" + minutes);
            }
        } else System.out.println("Рейсы между городами не найдены");
    }

    public static void difAvgMedian(Tickets tickets, String origin, String destination) {
        List<Integer> prices = new ArrayList<>();
        for (Ticket ticket : tickets.tickets) {
            if (!ticket.origin.equals(origin) || !ticket.destination.equals(destination))
                continue;

            prices.add(ticket.price);
        }

        if (!prices.isEmpty()) {
            int sum = 0;
            for (int price : prices)
                sum += price;
            double avg = (double) sum / prices.size();

            Collections.sort(prices);
            double median;
            if (prices.size() % 2 == 0)
                median = ((double)prices.get(prices.size()/2) + (double)prices.get(prices.size()/2 - 1))/2;
            else
                median = (double)prices.get(prices.size()/2);

            System.out.println("Разница между средней ценой и медианой = " + (avg - median));
        } else System.out.println("Рейсы между городами не найдены");
    }

    public static LocalDateTime dateTimeFormat(String date, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yy H:m");
        return LocalDateTime.parse(date + " " + time, formatter);
    }
}