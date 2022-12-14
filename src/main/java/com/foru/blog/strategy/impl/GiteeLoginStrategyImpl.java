package com.foru.blog.strategy.impl;import com.alibaba.fastjson.JSON;import com.alibaba.fastjson.JSONObject;import com.foru.blog.config.properties.GiteeProperties;import com.foru.blog.constant.SocialLoginConst;import com.foru.blog.dto.gitee.GiteeTokenDTO;import com.foru.blog.dto.blog.SocialTokenDTO;import com.foru.blog.dto.blog.SocialUserInfoDTO;import com.foru.blog.dto.gitee.GiteeUserInfoDTO;import com.foru.blog.enums.LoginTypeEnum;import com.foru.blog.vo.gitee.GiteeLoginVO;import okhttp3.*;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import org.springframework.web.client.RestTemplate;import java.io.IOException;/** * @ClassName GiteeLoginStrategyImpl * @Author 9527 */@Service("giteeLoginStrategyImpl")public class GiteeLoginStrategyImpl extends AbstractSocialLoginStrategyImpl{    private GiteeProperties giteeProperties;    @Autowired    public void setGiteeProperties(GiteeProperties giteeProperties) {        this.giteeProperties = giteeProperties;    }    private RestTemplate restTemplate;    @Autowired    public void setRestTemplate(RestTemplate restTemplate) {        this.restTemplate = restTemplate;    }    private Logger logger = LoggerFactory.getLogger(GiteeLoginStrategyImpl.class);    /**     * 获取第三方token信息     *     * @param data 数据     * @return {@link SocialTokenDTO} 第三方token信息     */    @Override    public SocialTokenDTO getSocialToken(String data) {        GiteeLoginVO giteeLoginVO = JSON.parseObject(data, GiteeLoginVO.class);        // 获取微博token信息        GiteeTokenDTO giteeToken = getGiteeToken(giteeLoginVO);        // 返回token信息        return SocialTokenDTO.builder()                //.openId(weiboToken.getUid())                .accessToken(giteeToken.getAccessToken())                .loginType(LoginTypeEnum.GITEE.getType())                .build();    }    /**     * 获取第三方用户信息     *     * @param socialTokenDTO 第三方token信息     * @return {@link SocialUserInfoDTO} 第三方用户信息     */    @Override    public SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialTokenDTO) {        String json = "";        OkHttpClient client = new OkHttpClient();        // 通过该地址能够获取到用户信息        String url = giteeProperties.getUserInfoUrl() + socialTokenDTO.getAccessToken();        Request request = new Request.Builder()                .addHeader("Content-Type","application/json;charset=UTF-8")                .addHeader("Accept","application/json")                .get()                .url(url)                .build();        try {            Response response = client.newCall(request).execute();            json = response.body().string();            logger.info("Gitee getSocialUserInfo response data is :{}",json);        } catch (IOException e) {            e.printStackTrace();        }        GiteeUserInfoDTO info = JSON.parseObject(json,GiteeUserInfoDTO.class);        SocialUserInfoDTO dto = SocialUserInfoDTO.builder()                .nickname(info.getName())                .avatar(replaceHttp(info.getAvatar_url()))                .email(info.getEmail())                .thirdId(info.getId().toString())                .build();        return dto;    }    /**     * 获取Giteetoken信息     *     * @param giteeLoginVO Gitee登录信息     * @return {@link GiteeLoginVO} Gitee登录信息token     */    public GiteeTokenDTO getGiteeToken(GiteeLoginVO giteeLoginVO) {        OkHttpClient client = new OkHttpClient();        // 通过该地址能够获取到access_token        String url = giteeProperties.getAccessTokenUrl();        // 封装请求参数        RequestBody requestBody = new FormBody.Builder()                .add(SocialLoginConst.GRANT_TYPE, giteeProperties.getGrantType())                .add(SocialLoginConst.CODE, giteeLoginVO.getCode())                .add(SocialLoginConst.CLIENT_ID, giteeProperties.getAppId())                .add(SocialLoginConst.REDIRECT_URI, giteeProperties.getRedirectUrl())                .add(SocialLoginConst.CLIENT_SECRET, giteeProperties.getAppSecret())                .build();        Request request = new Request.Builder()                .addHeader("Content-Type","application/json;charset=UTF-8")                .addHeader("Accept","application/json")                .post(requestBody)                .url(url).build();        String accessKey = "";        Integer expires = null;        try {            Response response = client.newCall(request).execute();            String json = response.body().string();            logger.info("getGiteeToken response data is :{}",json);            // 获取json串中的access_token属性            accessKey = (String) JSONObject.parseObject(json).get(SocialLoginConst.ACCESS_TOKEN);            expires = (Integer) JSONObject.parseObject(json).get(SocialLoginConst.EXPIRES_IN);        } catch (IOException e) {            e.printStackTrace();        }        GiteeTokenDTO data = GiteeTokenDTO.builder().accessToken(accessKey)                .appId(giteeProperties.getAppId())                .appSecret(giteeProperties.getAppSecret())                .redirectUrl(giteeProperties.getRedirectUrl())                .code(giteeLoginVO.getCode())                .expires(expires).build();        return data;    }//    private GiteeTokenDTO getGiteeToken(GiteeLoginVO giteeLoginVO) {//        // 根据code换取giteeuid和accessToken//        MultiValueMap<String, String> giteeData = new LinkedMultiValueMap<>();//        // 定义giteetoken请求参数//        giteeData.add(SocialLoginConst.CLIENT_ID, giteeProperties.getAppId());//        giteeData.add(SocialLoginConst.CLIENT_SECRET, giteeProperties.getAppSecret());//        giteeData.add(SocialLoginConst.REDIRECT_URI, giteeProperties.getRedirectUrl());//        giteeData.add(SocialLoginConst.CODE, giteeLoginVO.getCode());//        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();//        headers.add("application/json","charset=utf-8");//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(giteeData, headers);//        try {//            return restTemplate.exchange(giteeProperties.getAccessTokenUrl(), HttpMethod.POST, requestEntity, GiteeTokenDTO.class).getBody();//        } catch (Exception e) {//            throw new BizException(WEIBO_LOGIN_ERROR);//        }//    }}