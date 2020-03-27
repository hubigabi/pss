package pl.utp.pss.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.utp.pss.PssApplication;
import pl.utp.pss.model.AutoCapacity;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.model.TransportType;
import pl.utp.pss.model.User;
import pl.utp.pss.repository.DelegationRepository;
import pl.utp.pss.repository.UserRepository;
import pl.utp.pss.service.DelegationService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = PssApplication.class)
@AutoConfigureMockMvc
public class DelegationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private DelegationRepository delegationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void addDelegation() throws Exception {
        User user = userRepository.findAll().get(0);
        Delegation delegation = new Delegation("Delegation description", LocalDate.now(), LocalDate.now().plusDays(8),
                100, 1, 1, 2, TransportType.CAR, 0,
                AutoCapacity.OVER_900, 190.00, 150, 50, 40);


        List<Delegation> allByUserIdBefore = delegationService.getAllByUserId(user.getId());

        mvc.perform(MockMvcRequestBuilders.post("/api/delegation/addDelegation?&userId=" + user.getId())
                .content(asJsonString(delegation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        List<Delegation> allByUserIdAfter = delegationService.getAllByUserId(user.getId());

        //Delete all delegations which were before
        //Should remain only one
        allByUserIdAfter.removeAll(allByUserIdBefore);

        assertTrue(allByUserIdAfter.size() == 1);
        assertTrue(allByUserIdAfter.get(0).getDescription().equals(delegation.getDescription()));
        assertTrue(allByUserIdAfter.get(0).getDateTimeStart().equals(delegation.getDateTimeStart()));
    }

    @Test
    public void removeDelegation() throws Exception {
        User user = userRepository.findAll().get(0);
        List<Delegation> allByUserId = delegationService.getAllByUserId(user.getId());
        Delegation delegationToDelete = allByUserId.get(0);

        mvc.perform(MockMvcRequestBuilders.delete("/api/delegation/removeDelegation?userId=" + user.getId()
                + "&delegationId=" + delegationToDelete.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(allByUserId.size() >= 1);
        assertFalse(delegationService.getAllByUserId(user.getId()).contains(delegationToDelete));
        assertNull(delegationRepository.findById(delegationToDelete.getId()).orElse(null));
    }

    @Test
    public void changeDelegation() throws Exception {
        Delegation oldDelegation = delegationRepository.findAll().get(0);

        Delegation newDelegation = new Delegation("Changed delegation", LocalDate.now().plusDays(9), LocalDate.now().plusDays(18),
                120, 2, 1, 1, TransportType.BUS, 18,
                AutoCapacity.NONE, 70, 180, 40, 50);

        mvc.perform(MockMvcRequestBuilders.put("/api/delegation/changeDelegation?delegationId=" + oldDelegation.getId())
                .content(asJsonString(newDelegation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Delegation modifiedDelegation = delegationRepository.findById(oldDelegation.getId()).get();

        assertEquals(modifiedDelegation.getUser(), oldDelegation.getUser());
        assertEquals(modifiedDelegation.getId(), oldDelegation.getId());

        assertEquals(modifiedDelegation.getDescription(), newDelegation.getDescription());
        assertEquals(modifiedDelegation.getDateTimeStart(), newDelegation.getDateTimeStart());
        assertEquals(modifiedDelegation.getAccommodationPrice(), newDelegation.getAccommodationPrice(), 0.1);
        assertEquals(modifiedDelegation.getBreakfastNumber(), newDelegation.getBreakfastNumber());
        assertEquals(modifiedDelegation.getDinnerNumber(), newDelegation.getDinnerNumber());
        assertEquals(modifiedDelegation.getTicketPrice(), newDelegation.getTicketPrice(), 0.1);
        assertEquals(modifiedDelegation.getAccommodationPrice(), newDelegation.getAccommodationPrice(), 0.1);
        assertEquals(modifiedDelegation.getOtherOutlayDesc(), newDelegation.getOtherOutlayDesc(), 0.1);
        assertEquals(modifiedDelegation.getOtherOutlayPrice(), newDelegation.getOtherOutlayPrice(), 0.1);
    }

    @Test
    public void getAllDelegations() throws Exception {
        List<Delegation> delegations = delegationRepository.findAll();

        mvc.perform(MockMvcRequestBuilders.get("/api/delegation/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json;"))
                .andExpect(jsonPath("$.*", hasSize(delegations.size())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getAllDelegationsOrderByDateStartDesc() throws Exception {
        List<Delegation> delegations = delegationRepository.findAll();

        mvc.perform(MockMvcRequestBuilders.get("/api/delegation/allOrderByDateStartDesc"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json;"))
                .andExpect(jsonPath("$.*", hasSize(delegations.size())))
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString();
                    System.out.println(json);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                    List<Delegation> delegationsFromJson = mapper.readValue(json, new TypeReference<List<Delegation>>() {
                    });

                    List<Delegation> sortedDelegations = delegationsFromJson.stream()
                            .sorted(Comparator.comparing(Delegation::getDateTimeStart, Comparator.reverseOrder()))
                            .collect(Collectors.toList());

                    assertEquals(delegationsFromJson, sortedDelegations);
                })
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getAllDelegationsByUserOrderByDateStartDesc() throws Exception {
        User user = userRepository.findAll().get(0);

        List<Delegation> delegations = delegationService.getAllByUserId(user.getId());

        mvc.perform(MockMvcRequestBuilders.get("/api/delegation/allByUserIdOrderOrderByDateStartDesc?id=" + user.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType("application/json;"))
                .andExpect(jsonPath("$.*", hasSize(delegations.size())))
                .andDo(mvcResult -> {
                    String json = mvcResult.getResponse().getContentAsString();
                    System.out.println(json);
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.registerModule(new JavaTimeModule());
                    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                    List<Delegation> delegationsFromJson = mapper.readValue(json, new TypeReference<List<Delegation>>() {
                    });

                    List<Delegation> sortedDelegations = delegationsFromJson.stream()
                            .sorted(Comparator.comparing(Delegation::getDateTimeStart, Comparator.reverseOrder()))
                            .collect(Collectors.toList());

                    List<User> users = delegations.stream()
                            .map(Delegation::getUser)
                            .distinct()
                            .collect(Collectors.toList());

                    assertEquals(delegationsFromJson, sortedDelegations);
                    assertEquals(users.size(), 1);
                    assertEquals(users.get(0), user);

                })
                .andDo(MockMvcResultHandlers.print());
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}