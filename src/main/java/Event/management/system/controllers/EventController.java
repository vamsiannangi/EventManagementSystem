package Event.management.system.controllers;

import Event.management.system.dto.EventDto;
import Event.management.system.entities.Event;
import Event.management.system.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/events")
public class EventController {
private EventService eventService;

@Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/add")
    public Event createEvent(@RequestBody Event event){
     Event event1= eventService.save(event);
      return event1;
    }

    @GetMapping("/find")
    public Page<EventDto> findEvents(@RequestParam double latitude,
                                     @RequestParam double longitude,
                                     @RequestParam String date,
                                     @RequestParam(name = "page", defaultValue = "1" ,required=false) int page,
                                     @RequestParam(name = "size", defaultValue = "10", required = false) int size) {

        Pageable pageable = createPageable(page, size);
        Page<EventDto> eventsPage;
        List<String> datesFromCurrentDate = eventService.getNext14Days(date);
        Collections.sort(datesFromCurrentDate);
        eventsPage = eventService.findEvents(latitude, longitude, datesFromCurrentDate, pageable);
        return eventsPage;
    }

    private Pageable createPageable(int page, int size) {
        // Adjust the page number to be 0-based
        int adjustedPage = Math.max(page - 1, 0);
        return PageRequest.of(adjustedPage, size);
    }

    }
