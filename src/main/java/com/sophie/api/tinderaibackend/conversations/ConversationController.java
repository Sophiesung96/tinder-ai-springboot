package com.sophie.api.tinderaibackend.conversations;

import com.sophie.api.tinderaibackend.conversations.dto.ConversationRequestDto;
import com.sophie.api.tinderaibackend.profiles.ProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class ConversationController {

    private final ProfileRepository profileRepository;
    private final ConversationRepository conversationRepository;

    public ConversationController(ProfileRepository profileRepository, ConversationRepository conversationRepository) {
        this.profileRepository = profileRepository;
        this.conversationRepository = conversationRepository;
    }

    @PostMapping("/conversations")
    public Conversation createNewConversation(@Valid @RequestBody ConversationRequestDto conversationRequestDto){

       profileRepository.findById(conversationRequestDto.getProfileId())
               .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND
               ,"Unable to find profile with this ID: " + conversationRequestDto.getProfileId()));
       Conversation conversation=new Conversation(
               UUID.randomUUID().toString(),
               conversationRequestDto.getProfileId(),
               new ArrayList<>()
       );
       conversationRepository.save(conversation);
       return conversation;
    }

    @GetMapping("/conversations/{conversationId}")
    public Conversation getConversation(@PathVariable String conversationId){
        Conversation conversation=conversationRepository.findById(conversationId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unable to find conversation with this ID: " + conversationId));
                return conversation;
    }


    @PostMapping("/conversations/{conversationId}")
    public Conversation addMessageToConversation(@PathVariable String conversationId, @Valid @RequestBody ChatMessage chatMessage){
        Conversation conversation=conversationRepository.findById(conversationId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Unable to find conversation with this ID: "+conversationId));
        profileRepository.findById(chatMessage.authorId()).orElseThrow(()-> new ResponseStatusException
                (HttpStatus.NOT_FOUND,"Unable to find profile with this ID: "+chatMessage.authorId()));
        ChatMessage messageWithTime=new ChatMessage(
                chatMessage.messageText(),
                chatMessage.authorId(),
                LocalDateTime.now()
        );
        conversation.messages().add(chatMessage);
        conversationRepository.save(conversation);
        return conversation;



     }


}
