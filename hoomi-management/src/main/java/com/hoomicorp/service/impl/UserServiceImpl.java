package com.hoomicorp.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.hoomicorp.exception.UserAlreadyExistsException;
import com.hoomicorp.model.dto.UserInfoDto;
import com.hoomicorp.model.entity.User;
import com.hoomicorp.model.entity.enums.Status;
import com.hoomicorp.model.request.RegistrationRequest;
import com.hoomicorp.model.request.Request;
import com.hoomicorp.model.request.VerifyUserFieldsRequest;
import com.hoomicorp.model.response.Response;
import com.hoomicorp.model.response.VerifyUserFieldsResponse;
import com.hoomicorp.repository.UserRepository;
import com.hoomicorp.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^([\\w-\\.]+){1,64}@([\\w&&[^_]]+){2,255}(.[a-z]{2,3})+$|^$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^+\\d{15}$");

    private final Firestore firestore;
    private final UserRepository userRepository;
    private final CollectionReference userInfoCollection;

    public UserServiceImpl(Firestore firestore, UserRepository userRepository) {
        this.firestore = firestore;
        this.userRepository = userRepository;
        this.userInfoCollection = firestore.collection("user-info");
    }

    @PostConstruct
    private void init() {
//        CollectionReference streams = firestore.collection("user-streams");
//        streams.addSnapshotListener((snapshots, err) -> {
//            if (err != null) {
//                System.err.println("Listen failed: " + err);
//                return;
//            }
//
//            for (DocumentChange dc : snapshots.getDocumentChanges()) {
//                switch (dc.getType()) {
//                    case ADDED:
//                        UserStreamDto streamDto = dc.getDocument().toObject(UserStreamDto.class);
//                        System.out.println("New city: " + streamDto);
//                        break;
//                    case MODIFIED:
//                        System.out.println("Modified city: " + dc.getDocument().getData());
//                        break;
//                    case REMOVED:
//                        System.out.println("Removed city: " + dc.getDocument().getData());
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }


    public User findUser(final String login, final String password) {
        User user;
        if (EMAIL_PATTERN.matcher(login).matches()) {
            user = userRepository.findUserByEmailAndPassword(login, password);
        } else if (PHONE_PATTERN.matcher(login).matches()) {
            user = userRepository.findUserByPhoneAndPassword(login, password);
        } else {
            user = userRepository.findUserByUsernameAndPassword(login, password);
        }

        return user;
    }

    public UserInfoDto saveUser(final RegistrationRequest registrationRequest) {
        final String email = registrationRequest.getEmail();
        final String phoneNum = registrationRequest.getPhoneNum();
        final String username = registrationRequest.getUsername();
        final String password = registrationRequest.getPassword();

        User user = null;
        if (Objects.nonNull(email)) {
            user = userRepository.findUserByEmail(email);
        } else if (Objects.nonNull(phoneNum)) {
            user = userRepository.findUserByPhone(phoneNum);
        }

        if (Objects.nonNull(user)) {
            throw new UserAlreadyExistsException("");
        }

        user = userRepository.findUserByUsername(username);
        if (Objects.nonNull(user)) {
            throw new UserAlreadyExistsException("");
        }

        user = userRepository.save(new User(email, phoneNum, username, password, LocalDate.now(),  Status.ACTIVE));

        final UserInfoDto info = UserInfoDto.builder().id(user.getId()).name(user.getUsername())
                .profileImageLink("https://hoomi-images.s3.eu-central-1.amazonaws.com/events-images/pubg.jpg").build();

        userInfoCollection.document(user.getId()).set(info);

        //todo change profile link
        return info;
    }

    @Override
    public Response verifyUserFields(final Request request) {
        final List<VerifyUserFieldsRequest> body = (List<VerifyUserFieldsRequest>) request.getBody();
        if (Objects.isNull(body)) {
            throw new InputMismatchException("Body cannot be null or empty");
        }

        final List<VerifyUserFieldsResponse> errResponses = body.stream()
                .map(this::verifyField)
                .filter(VerifyUserFieldsResponse::isError)
                .collect(Collectors.toList());

        if (errResponses.size() > 0) {
            return Response.builder().error(true).body(errResponses).uuid(request.getUuid()).build();
        } else {
            return Response.builder().uuid(request.getUuid()).messages(List.of("Success")).build();
        }
    }


    private VerifyUserFieldsResponse verifyField(final VerifyUserFieldsRequest fieldRequest) {
        final String fieldId = fieldRequest.getId();
        final String value = fieldRequest.getFieldValue();
        if (Objects.equals(fieldId, "1")) {
            final User userByUsername = userRepository.findUserByUsername(value);
            if (Objects.nonNull(userByUsername)) {
                return generateErrResponse(fieldId, "Username already exists");
            }
        } else if (Objects.equals(fieldId, "2")) {
            final User userByEmail = userRepository.findUserByEmail(value);
            if (Objects.nonNull(userByEmail)) {
                return generateErrResponse(fieldId, "Email already exists");
            }
        } else if (Objects.equals(fieldId, "3")){
            final User userByPhone = userRepository.findUserByPhone(value);
            if (Objects.nonNull(userByPhone)) {
                return generateErrResponse(fieldId, "Email already exists");
            }
        } else {
            throw new InputMismatchException("Incorrect input");
        }

        return VerifyUserFieldsResponse.builder().build();
    }

    private VerifyUserFieldsResponse generateErrResponse(String fieldId, String message) {
        return VerifyUserFieldsResponse.builder().id(fieldId)
                .error(true)
                .message(message).build();
    }

}
