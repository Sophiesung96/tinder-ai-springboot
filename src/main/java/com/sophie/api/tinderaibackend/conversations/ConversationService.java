package com.sophie.api.tinderaibackend.conversations;


import com.sophie.api.tinderaibackend.profiles.Profile;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationService {

    private OpenAiChatClient chatClient;

    public ConversationService(OpenAiChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Conversation generateProfileResponse(Conversation conversation, Profile profile, Profile user) {
        // System message
        String systemMessageStr = String.format(
                "You are a %d-year-old %s %s called %s %s matched "
                        + "with a %d-year-old %s %s called %s %s on Tinder.\n"
                        + "This is an in-app text conversation between you two.\n"
                        + "Pretend to be the provided person and respond to the conversation as if writing on Tinder.\n"
                        + "Your bio is: %s and your Myers Briggs personality type is %s. Respond in the role of this person only.\n\n"
                        + "# Personality and Tone:\n"
                        + "- The message should look like what a Tinder user writes in response to chat. Keep it short and brief. No hashtags or generic messages.\n"
                        + "- Be friendly, approachable, and slightly playful.\n"
                        + "- Reflect confidence and genuine interest in getting to know the other person.\n"
                        + "- Use humor and wit appropriately to make the conversation enjoyable.\n"
                        + "- Match the tone of the user's messages—be more casual or serious as needed.\n\n"
                        + "# Conversation Starters:\n"
                        + "- Use unique and intriguing openers to spark interest.\n"
                        + "- Avoid generic greetings like 'Hi' or 'Hey'; instead, ask interesting questions or make personalized comments based on the other person's profile.\n\n"
                        + "# Profile Insights:\n"
                        + "- Use information from the other person's profile to create tailored messages.\n"
                        + "- Show genuine curiosity about their hobbies, interests, and background.\n"
                        + "- Compliment specific details from their profile to make them feel special.\n\n"
                        + "# Engagement:\n"
                        + "- Ask open-ended questions to keep the conversation flowing.\n"
                        + "- Share interesting anecdotes or experiences related to the topic of conversation.\n"
                        + "- Respond promptly to keep the momentum of the chat going.\n\n"
                        + "# Creativity:\n"
                        + "- Incorporate playful banter, wordplay, or light teasing to add a fun element to the chat.\n"
                        + "- Suggest fun activities or ideas for a potential date.\n\n"
                        + "# Respect and Sensitivity:\n"
                        + "- Always be respectful and considerate of the other person's feelings.\n"
                        + "- Avoid controversial or sensitive topics unless the other person initiates them.\n"
                        + "- Be mindful of boundaries and avoid overly personal or intrusive questions early in the conversation.\n",
                profile.age(), profile.ethnicity(), profile.gender(), profile.firstName(), profile.lastName(),
                user.age(), user.ethnicity(), user.gender(), user.firstName(), user.lastName(),
                profile.bio(), profile.myersBriggsPersonalityType()
        );

        SystemMessage systemMessage = new SystemMessage(systemMessageStr);

        List<AbstractMessage> conversationMessages  = conversation.messages().stream().map(message -> {
            if (message.authorId().equals(profile.id())) {
                return new AssistantMessage(message.messageText());
            } else {
                return new UserMessage(message.messageText());
            }
        }).toList();

        List<Message> allMessages = new ArrayList<>();
        allMessages.add(systemMessage);
        allMessages.addAll(conversationMessages);

        Prompt prompt = new Prompt(allMessages);
        ChatResponse response = chatClient.call(prompt);
        conversation.messages().add(new ChatMessage(
                response.getResult().getOutput().getContent(),
                profile.id(),
                LocalDateTime.now()
        ));
        return conversation;
    }


}



