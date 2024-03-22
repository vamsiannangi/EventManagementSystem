package Event.management.system.services;

import Event.management.system.dto.EventDto;
import Event.management.system.entities.Event;
import Event.management.system.repositories.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {
    private EventRepository eventRepository;
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    @Value("${distance.api.key}")
    private String distanceApiKey;
    @Value("${weather.api.key}")
    private String weatherApiKey;
    public Event save(Event event) {
        eventRepository.save(event);
        return event;
    }

    public Page<EventDto> findEvents(double latitude, double longitude, List<String> date,Pageable pageable) {
         List<Event> events=fetchEvents(date);
        List<Event> eventsWithWeather = retrieveWeatherDetails(events);
        List<Event> eventsWithDistance = calculateDistances(events, latitude, longitude);
        return mapToEventDtoList(eventsWithDistance, pageable);
    }

    public List<Event> fetchEvents(List<String> dates) {
        List<Event> allEvents = new ArrayList<>();

        for (String date : dates) {
            List<Event> events = eventRepository.findAllByLatitudeLongitudeDate(date);
            allEvents.addAll(events);
        }

        return allEvents;
    }


    public List<Event> retrieveWeatherDetails(List<Event> events) {

        for (Event event : events) {
            RestTemplate restTemplate = new RestTemplate();
            String weatherUrl = weatherApiKey  + event.getCityName() + "&date=" + event.getDate();
            String weatherInfo = restTemplate.getForObject(weatherUrl, String.class);

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(weatherInfo);
                String weather = jsonNode.get("weather").asText();
                event.setWeather(weather);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing weather information", e);
            }
        }

        return events;
    }

    private List<Event> calculateDistances(List<Event> events, double latitude, double longitude) {
        RestTemplate restTemplate = new RestTemplate();

        for (Event event : events) {
         String distanceUrl=distanceApiKey+  latitude + "&longitude1=" + longitude + "&latitude2=" + event.getLatitude() + "&longitude2=" + event.getLongitude();
           String distanceResponse = restTemplate.getForObject(distanceUrl, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(distanceResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            String distanceStr = jsonNode.get("distance").asText();
            double distance = Double.parseDouble(distanceStr);
            event.setDistance(distance);
        }

        return events;
    }


public Page<EventDto> mapToEventDtoList(List<Event> events, Pageable pageable) {
    int start = (int) pageable.getOffset();
    int end = (int) Math.min((start + pageable.getPageSize()), events.size());
    List<EventDto> eventDtos = new ArrayList<>();
    for (int i = start; i < end; i++) {
        Event event = events.get(i);
        EventDto eventDto = mapToEventDto(event);
        eventDtos.add(eventDto);
    }
    return new PageImpl<>(eventDtos, pageable, events.size());
}


    private EventDto mapToEventDto(Event event) {
    EventDto eventDto = new EventDto();
    eventDto.setEventName(event.getEventName());
    eventDto.setCityName(event.getCityName());
    eventDto.setDate(event.getDate());
    eventDto.setWeather(event.getWeather());
    eventDto.setDistance(event.getDistance());
    return eventDto;
}

    public static List<String> getNext14Days(String startDateText) {
        List<String> next14Days = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(startDateText);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 14; i++) {
            LocalDate nextDate = startDate.plusDays(i);
            if (nextDate.getDayOfMonth() == 1 && i > 0) {
                nextDate = nextDate.plusMonths(1); // Move to the next month
                if (nextDate.getMonthValue() == 1) {
                    nextDate = nextDate.plusYears(1); // Move to the next year
                }
            }
            next14Days.add(nextDate.format(formatter));
        }

        return next14Days;
    }

}



