package pl.utp.pss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.utp.pss.model.Delegation;
import pl.utp.pss.service.DelegationService;
import pl.utp.pss.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/delegation")
public class DelegationController {

    DelegationService delegationService;
    UserService userService;

    @Autowired
    public DelegationController(DelegationService delegationService, UserService userService) {
        this.delegationService = delegationService;
        this.userService = userService;
    }

    @PostMapping("/addDelegation")
    public void addDelegation(long userId, @RequestBody Delegation delegation) {
        delegation.setUser(userService.getUser(userId));
        delegationService.createDelegation(delegation);
    }

    @DeleteMapping("/removeDelegation")
    public void removeDelegation(long userId, long delegationId) {
        delegationService.deleteDelegation(userId, delegationId);
    }

    @PutMapping("/changeDelegation")
    void changeDelegation(long delegationId, @RequestBody Delegation delegation) {
        delegation.setId(delegationId);
        delegation.setUser(delegationService.getDelegation(delegationId).getUser());
        delegationService.updateDelegation(delegation);
    }

    @GetMapping("/all")
    public List<Delegation> getAllDelegations() {
        return delegationService.getAllDelegations();
    }

    @GetMapping("/allOrderByDateStartDesc")
    public List<Delegation> getAllDelegationsOrderByDateStartDesc() {
        return delegationService.getAllDelegations()
                .stream()
                .sorted(Comparator.comparing(Delegation::getDateTimeStart,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @GetMapping("/allByUserIdOrderOrderByDateStartDesc")
    public List<Delegation> getAllDelegationsByUserOrderByDateStartDesc(long id) {
        return delegationService.getAllByUserId(id)
                .stream()
                .sorted(Comparator.comparing(Delegation::getDateTimeStart,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/report/{delegationId}", produces = "application/pdf")
    public ResponseEntity<InputStreamResource> downloadReport(@PathVariable("delegationId") long delegationId) throws IOException {

        String pathToReport = delegationService.generateReport(delegationId);
        InputStream is = new FileInputStream(pathToReport);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
        headers.add("Content-Disposition", "filename=" + delegationId + ".pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        ResponseEntity<InputStreamResource> response = new ResponseEntity<>(new InputStreamResource(is), headers, HttpStatus.OK);

        return response;
    }

}
