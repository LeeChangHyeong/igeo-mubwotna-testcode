package service;

import com.sparta.igeomubwotna.dto.Response;
import com.sparta.igeomubwotna.dto.SignupRequestDto;
import com.sparta.igeomubwotna.dto.UserUpdateRequestDto;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.repository.UserRepository;
import com.sparta.igeomubwotna.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;  // 모킹된 객체를 실제 서비스에 주입

    private User user;

    @BeforeEach
    void setup() {
        user = new User("lchNumber9", "Dlckdgud11!", "이창형", "shlee509@nate.com", "안녕");
        user.setId(1L);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testSignupSuccess() {
        SignupRequestDto requestDto = mock(SignupRequestDto.class);
        when(requestDto.getUserId()).thenReturn("lchNumber9");
        when(requestDto.getName()).thenReturn("이창형");
        when(requestDto.getDescription()).thenReturn("안녕");
        when(requestDto.getPassword()).thenReturn("Dlckdgud11!");
        when(requestDto.getEmail()).thenReturn("shlee509@nate.com");

        BindingResult bindingResult = mock(BindingResult.class);
        Response response = new Response(HttpStatus.OK.value(), "회원가입에 성공하였습니다.");
        ResponseEntity<Response> responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);

        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        ResponseEntity<Response> result = userService.signup(requestDto, bindingResult);

        assertNotNull(result);
        assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        assertEquals(responseEntity.getBody().getMessage(), result.getBody().getMessage());
    }

    @Test
    @DisplayName("비밀번호 수정 테스트")
    void testUpdatePasswordSuccess() {
        UserUpdateRequestDto userUpdateRequestDto = mock(UserUpdateRequestDto.class);
        when(userUpdateRequestDto.getName()).thenReturn("이창형여영");
        when(userUpdateRequestDto.getCurrentPassword()).thenReturn("Dlckdgud11!");
        when(userUpdateRequestDto.getNewPassword()).thenReturn("Dlckdgud11!!!");
        when(userUpdateRequestDto.getDescription()).thenReturn("수정했");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(user.getPassword(), userUpdateRequestDto.getCurrentPassword())).willReturn(true);
        given(passwordEncoder.matches(userUpdateRequestDto.getNewPassword(), userUpdateRequestDto.getCurrentPassword())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("newEncodedPassword");

        Response response = new Response(HttpStatus.OK.value(), "프로필 정보를 성공적으로 수정하였습니다.");
        ResponseEntity<Response> responseEntity = ResponseEntity.status(HttpStatus.OK).body(response);

        ResponseEntity<Response> result = userService.updateUserProfile(userUpdateRequestDto, user.getId());

        assertNotNull(result);
        assertEquals(responseEntity.getStatusCode(), result.getStatusCode());
        assertEquals(responseEntity.getBody().getMessage(), result.getBody().getMessage());
    }
}
