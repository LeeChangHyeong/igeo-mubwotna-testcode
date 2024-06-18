package service;

import com.sparta.igeomubwotna.dto.Response;
import com.sparta.igeomubwotna.dto.SignupRequestDto;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.repository.UserRepository;
import com.sparta.igeomubwotna.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    @DisplayName("회원가입 테스트")
    void testSignupSuccess() {
        SignupRequestDto requestDto = mock(SignupRequestDto.class);
        when(requestDto.getUserId()).thenReturn("lchNumber9");
        when(requestDto.getName()).thenReturn("이창형");
        when(requestDto.getDescription()).thenReturn("안녕");
        when(requestDto.getPassword()).thenReturn("Dlckdgud11!");
        when(requestDto.getEmail()).thenReturn("shlee509@nate.com");

        user = new User("lchNumber9", "encodedPassword", "이창형", "shlee509@nate.com", "안녕");
        user.setId(1L);

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
}
