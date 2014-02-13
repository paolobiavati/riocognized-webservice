package fr.olympicinsa.riocognized;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import fr.olympicinsa.riocognized.model.*;
import fr.olympicinsa.riocognized.repository.*;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;

/**
 * Handles requests for the application home page.
 */
@Controller
@ComponentScan("fr.olympicinsa.riocognized.repository")
public class AthleteController {

    @Autowired
    private AthleteRepository athleteRepository;

    @RequestMapping(value="/api", method = RequestMethod.GET)
    public 
   @ResponseBody 
        String listAthleteJson(ModelMap model) throws JSONException {
        JSONArray athleteArray = new JSONArray();
        for (Athlete athlete : athleteRepository.findAll()) {
            JSONObject athleteJSON = new JSONObject();
            athleteJSON.put("id", athlete.getId());
            athleteJSON.put("firstName", athlete.getSurname());
            athleteJSON.put("lastName", athlete.getName());
            athleteJSON.put("age", athlete.getAge());
            athleteJSON.put("country", athlete.getCountry());
            athleteJSON.put("sport", athlete.getSport());
            athleteJSON.put("palmares", athlete.getContent());
            athleteArray.put(athleteJSON);
        }
        return athleteArray.toString();
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String listUsers(ModelMap model) {
        model.addAttribute("athlete", new Athlete());
        model.addAttribute("athletes", athleteRepository.findAll());
        return "athlete";
    }
 
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addUser(@ModelAttribute("athlete") Athlete athlete, BindingResult result) {
        athleteRepository.save(athlete);
        return "redirect:/";
    }

    @RequestMapping(value ="/delete/{athleteId}", method = RequestMethod.POST)
    public String deleteUser(@PathVariable("athleteId") Long athleteId) {

        athleteRepository.delete(athleteRepository.findOne(athleteId));

        return "redirect:/";
    }
}
