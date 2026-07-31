package com.example.myapp.controllers;

import com.example.myapp.annotation.TrackCall;
import com.example.myapp.common.ApiResult;
import com.example.myapp.common.BizException;
import com.example.myapp.common.ErrorCode;
import com.example.myapp.dto.BubbleSortRequest;
import com.example.myapp.dto.HashRequest;
import com.example.myapp.enums.ApiName;
import com.example.myapp.repositories.UserRepository;
import com.example.myapp.services.AlgoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 算法服务 REST 接口。
 * 使用 @TrackCall 注解触发 AOP 异步埋点，Controller 本身不直接调用 TrackService。
 */
@RestController
@RequestMapping("/api")
public class AlgoController {

    private final AlgoService algoService;
    private final UserRepository userRepository;

    @Autowired
    public AlgoController(AlgoService algoService, UserRepository userRepository) {
        this.algoService = algoService;
        this.userRepository = userRepository;
    }

    /**
     * W01 helloworld 接口：返回固定字符串 "Hello World"。
     */
    @TrackCall(ApiName.HELLOWORLD)
    @GetMapping("/helloworld")
    public ApiResult<String> helloworld(@RequestParam Long userId) {
        validateUserId(userId);
        String result = algoService.helloworld();
        return ApiResult.success(result);
    }

    /**
     * W02 哈希算法接口：对输入字符串计算 SHA-256 哈希值。
     */
    @TrackCall(ApiName.HASH)
    @PostMapping("/hash")
    public ApiResult<String> hash(@Valid @RequestBody HashRequest request) {
        validateUserId(request.getUserId());
        String hashResult = algoService.hash(request.getInput());
        return ApiResult.success(hashResult);
    }

    /**
     * W03 冒泡排序接口：对输入整数数组执行冒泡排序。
     */
    @TrackCall(ApiName.BUBBLE_SORT)
    @PostMapping("/bubble-sort")
    public ApiResult<int[]> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        validateUserId(request.getUserId());
        int[] sortedArr = algoService.bubbleSort(request.getArr());
        return ApiResult.success(sortedArr);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BizException(ErrorCode.ALGO_002, ErrorCode.MSG_USER_ID_NULL);
        }
        if (!userRepository.existsById(userId)) {
            throw new BizException(ErrorCode.ALGO_001, ErrorCode.MSG_USER_NOT_FOUND);
        }
    }
}
