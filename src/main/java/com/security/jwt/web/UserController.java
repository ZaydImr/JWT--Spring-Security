package com.security.jwt.web;

import com.security.jwt.entity.User;
import com.security.jwt.helpers.HttpResponse;
import com.security.jwt.helpers.exception.*;
import com.security.jwt.security.JwtTokenProvider;
import com.security.jwt.security.UserPrincipal;
import com.security.jwt.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.security.jwt.helpers.constant.FileConstant.*;
import static com.security.jwt.helpers.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping({"/", "user"})
public class UserController extends ExceptionHandling {

    @Autowired
    private IUserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    @PostMapping("login")
    public ResponseEntity<User> loginUser(@RequestBody User user){
        authenticate(user.getUsername(), user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }
    @GetMapping("list")
    public ResponseEntity<List<User>> findAllUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }
    @GetMapping("find/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username){
        return new ResponseEntity<>(userService.findUserByUsername(username), HttpStatus.OK);
    }
    @PostMapping("add")
    public ResponseEntity<User> addNewUser(@RequestBody User user, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User newUser = userService.addNewUser(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isNotLocked(),
                user.isActive(),
                profileImage
        );
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }
    @PostMapping("update")
    public ResponseEntity<User> updateUser(@RequestBody User user, @RequestParam("currentUsername") String currentUsername, @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User updateUser = userService.updateUser(
                currentUsername,
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isNotLocked(),
                user.isActive(),
                profileImage
        );
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }
    @GetMapping("resetPassword/{email}")
    public ResponseEntity<String> resetPassword(@PathVariable String email) throws EmailNotFoundException, MessagingException {
        userService.resetPassword(email);
        return new ResponseEntity<>("Reset email sent successfully to : " + email, HttpStatus.OK);
    }
    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

    /*       ProfilePicture       */
    @GetMapping(path = "image/{username}/{filename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable String username, @PathVariable String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + filename));
    }
    @GetMapping(path = "image/profile/{username}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getProfileTempImage(@PathVariable String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (InputStream inputStream = url.openStream()){
            int bytesRead;
            byte[] chunck = new byte[1024];
            while((bytesRead = inputStream.read(chunck)) > 0){
                byteArrayOutputStream.write(chunck, 0, bytesRead);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    @PostMapping("updateProfileImage")
    public ResponseEntity<User> updateProfileImage(@RequestParam String username, @RequestParam(value = "profileImage") MultipartFile profileImage ) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {
        User user = userService.updateProfileImage(username, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }
    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
