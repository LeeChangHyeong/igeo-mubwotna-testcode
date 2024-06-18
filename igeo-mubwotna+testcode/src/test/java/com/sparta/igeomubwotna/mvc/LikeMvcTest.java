package com.sparta.igeomubwotna.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.igeomubwotna.config.SecurityConfig;
import com.sparta.igeomubwotna.controller.LikeController;
import com.sparta.igeomubwotna.entity.User;
import com.sparta.igeomubwotna.security.UserDetailsImpl;
import com.sparta.igeomubwotna.service.LikeService;
import com.sparta.igeomubwotna.service.RecipeService;
import com.sparta.igeomubwotna.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {LikeController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        }
)

@MockBean(JpaMetamodelMappingContext.class)
public class LikeMvcTest {
    private MockMvc mvc;
    private Principal mockPrincipal;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    LikeService likeService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
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
    @DisplayName("레시피 좋아요 추가")
    void test1() throws Exception {
        // given
        User user = mockUserSetup();
        given(likeService.addRecipeLike(anyLong(), eq(user))).willReturn(ResponseEntity.status(HttpStatus.OK).body("좋아요 성공!"));

        // when - then
        mvc.perform(post("/api/recipe/{recipeId}/like", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .principal(mockPrincipal)
        )
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요 성공!"))
                .andDo(print());
    }

    @Test
    @DisplayName("레시피 좋아요 삭제")
    void test2() throws Exception {
        // given
        User user = mockUserSetup();
        given(likeService.removeRecipeLike(anyLong(), eq(user))).willReturn(ResponseEntity.status(HttpStatus.OK).body("좋아요 취소 성공!"));

        // when - then
        mvc.perform(delete("/api/recipe/{recipeLikeId}/like", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요 취소 성공!"))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요 추가")
    void test3() throws Exception {
        // given
        User user = mockUserSetup();
        given(likeService.addCommentLike(anyLong(), eq(user))).willReturn(ResponseEntity.status(HttpStatus.OK).body("좋아요 성공!"));

        // when - then
        mvc.perform(post("/api/comment/{commentId}/like", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요 성공!"))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 좋아요 삭제")
    void test4() throws Exception {
        // given
        User user = mockUserSetup();
        given(likeService.removeCommentLike(anyLong(), eq(user))).willReturn(ResponseEntity.status(HttpStatus.OK).body("좋아요 취소 성공!"));

        // when - then
        mvc.perform(delete("/api/comment/{commentLikeId}/like", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("좋아요 취소 성공!"))
                .andDo(print());
    }
}
