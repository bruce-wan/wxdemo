package io.example.wechat.controller;

import io.example.wechat.service.WechatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bruce.wan on 2021/2/25.
 */
@Slf4j
@RestController
@RequestMapping("/wechat/portal/{wxtype}")
public class WechatPortalController {
    private final WechatService wechatService;

    public WechatPortalController(WechatService wechatService) {
        this.wechatService = wechatService;
    }

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String bind(@PathVariable(name = "wxtype") String wxtype,
                       @RequestParam(name = "signature", required = false) String signature,
                       @RequestParam(name = "timestamp", required = false) String timestamp,
                       @RequestParam(name = "nonce", required = false) String nonce,
                       @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("\nReceive Wechat authentication message: signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]",
                signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("illegal request data!");
        }

        return wechatService.checkSignature(wxtype, timestamp, nonce, signature) ? echostr : "illegal request";
    }

    @PostMapping
    public String post(@PathVariable(name = "wxtype") String wxtype,
                       @RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam(name = "encrypt_type", required = false) String encryptType,
                       @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info("\nReceive WeChat request: [signature=[{}], encryptType=[{}], msgSignature=[{}], timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encryptType, msgSignature, timestamp, nonce, requestBody);

        if (!wechatService.checkSignature(wxtype, timestamp, nonce, signature)) {
            throw new IllegalArgumentException("illegal request！");
        }

        return wechatService.processMessage(wxtype, requestBody, encryptType, timestamp, nonce, msgSignature);
    }
}
