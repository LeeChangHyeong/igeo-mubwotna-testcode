package com.sparta.igeomubwotna.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.igeomubwotna.config.SecurityConfig;
import com.sparta.igeomubwotna.controller.CommentController;
import com.sparta.igeomubwotna.dto.CommentRequestDto;
import com.sparta.igeomubwotna.dto.CommentResponseDto;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.security.UserDetailsImpl;
import com.sparta.igeomubwotna.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = {CommentController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)

@MockBean(JpaMetamodelMappingContext.class)
public class CommentMvcTest {
    private MockMvc mvc; // MockMvc 객체 선언
    private Principal mockPrincipal; // 인증된 사용자 정보를 담을 Principal 객체
    @Autowired
    private WebApplicationContext context; // Spring의 WebApplicationContext 객체
    @Autowired
    private ObjectMapper objectMapper; // JSON 처리를 위한 ObjectMapper 객체
    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setup() {
        // MockMvc 객체 초기화 및 Spring Security 설정 적용
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter())) //기존 security filter 취소했으니 새로 만들어준 필터 넣어줌
                .build();
        Locale.setDefault(Locale.KOREAN); // 언어를 한국어로 설정
    }

    private User mockUserSetup() {
        // Mock 테스트 유저 생성
        String userId = "lcnNumber9";
        String password = "Dlckdgud11!";
        String name = "이창형";
        String email = "shlee509@nate.com";
        String description = "안녕하세요";

        User testUser = new User(userId, password, name, email, description);
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());

        return testUser;
    }

    @Test
    @DisplayName("Comment 작성")
    void test1() throws Exception {
        //given
        User user = mockUserSetup();
        CommentRequestDto commentRequestDto = Mockito.mock(CommentRequestDto.class);
        when(commentRequestDto.getContent()).thenReturn("안녕");
        given(commentService.createComment(any(commentRequestDto.getClass()), anyLong(), eq(user))).willReturn(ResponseEntity.status(HttpStatus.OK).body("comment가 등록되었습니다."));

        String postInfo = objectMapper.writeValueAsString(commentRequestDto);

        // when-then
        mvc.perform(post("/api/recipe/{recipeId}/comment", 1)
                        .content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("comment가 등록되었습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제")
    void testDeleteComment() throws Exception {
        // given
        User user = mockUserSetup();  //회원필요
        Long recipeId = 1L;
        Long commentId = 1L;

        doNothing().when(commentService).deleteComment(anyLong(), anyLong(), eq(user));

        // when - then
        mvc.perform(delete("/api/recipe/{recipeId}/comment/{commentId}", recipeId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())  //예측
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // 응답 컨텐츠 타입이 JSON인지 확인
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))  // Response 객체의 statusCode 필드 값이 200인지 확인
                .andExpect(jsonPath("$.message").value("댓글이 삭제되었습니다"))  // Response 객체의 message 필드 값 확인
                .andDo(print());
    }

    @Test
    @DisplayName("Comment 조회")
    void test3() throws Exception {
        // given
        User user = mockUserSetup();
        Long recipeId = 1L;
        CommentResponseDto commentResponseDto = Mockito.mock(CommentResponseDto.class);

        when(commentResponseDto.getId()).thenReturn(1L);
        when(commentResponseDto.getContent()).thenReturn("안녕");
        when(commentResponseDto.getUserId()).thenReturn(user.getUserId());
        when(commentResponseDto.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(commentResponseDto.getLikeCount()).thenReturn(1L);

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        commentResponseDtoList.add(commentResponseDto);

        given(commentService.getComment(anyLong())).willReturn(commentResponseDtoList);
        // when - then
        mvc.perform(get("/api/recipe/{recipeId}/comment", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())  // 예상 상태 코드
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))  // 응답 컨텐츠 타입이 JSON인지 확인
                .andExpect(jsonPath("$[0].id").value(commentResponseDto.getId())) // 첫 번째 댓글의 id 확인
                .andExpect(jsonPath("$[0].content").value(commentResponseDto.getContent())) // 첫 번째 댓글의 content 확인
                .andExpect(jsonPath("$[0].userId").value(user.getUserId())) // 첫 번째 댓글의 userId 확인
                .andExpect(jsonPath("$[0].likeCount").value(commentResponseDto.getLikeCount())) // 첫 번째 댓글의 likeCount 확인
                .andDo(print());
    }

    @Test
    @DisplayName("Comment 수정")
    void test4() throws Exception {
        // given
        User user = mockUserSetup();

        Long recipeId = 1L;
        Long commentId = 1L;

        CommentRequestDto commentRequestDto = Mockito.mock(CommentRequestDto.class);
        when(commentRequestDto.getContent()).thenReturn("수정");
        given(commentService.updateComment(eq(recipeId), eq(commentId), any(CommentRequestDto.class), eq(user))).willReturn(ResponseEntity.status(HttpStatus.OK).body("comment가 수정되었습니다."));

        String patchInfo = objectMapper.writeValueAsString(commentRequestDto);

        // when - then
        mvc.perform(patch("/api/recipe/{recipeId}/comment/{commentId}", recipeId, commentId)
                        .content(patchInfo) // 수정된 내용
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())  // 예상 상태 코드
                .andExpect(content().string("comment가 수정되었습니다."))
                .andDo(print());
    }
}
