package com.ozarychta.controller;

import com.ozarychta.model.Invitation;
import com.ozarychta.repository.InvitationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class InvitationController {

    @Autowired
    private InvitationRepository invitationRepository;

    @GetMapping("/invitations")
    public ResponseEntity getInvitations(@RequestHeader("authorization") String authString) {
        //add authentication
        return new ResponseEntity(invitationRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("/invitations")
    public @ResponseBody ResponseEntity createDay(@RequestHeader("authorization") String authString,
                                                  @Valid @RequestBody Invitation invitation) {
        //authorization and adding user to add
        return new ResponseEntity(invitationRepository.save(invitation), HttpStatus.OK);
    }
}
