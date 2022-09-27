package lkeleti.securitycors.controllers;

import lkeleti.securitycors.models.Coffee;
import lkeleti.securitycors.models.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/coffee")
//@CrossOrigin //Accept from anywhere
//@CrossOrigin(origins = "http://localhost:5173") //Accept from given host and port
public class CoffeeController {

    private List<Coffee> coffeeList = new ArrayList<>();

    public CoffeeController() {
        coffeeList.add(new Coffee(1,"Caffe Americano", Size.GRANDE));
        coffeeList.add(new Coffee(2,"Caffe Latte", Size.VENT));
        coffeeList.add(new Coffee(3,"Caffe Caramel Macchiato", Size.TALL));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    //@CrossOrigin(origins = "http://localhost:5173") //Accept from given host and port
    public List<Coffee> findAll() {
        return coffeeList;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Integer id) {
        coffeeList.removeIf(coffee->coffee.id().equals(id));
    }
}
