package com.example.mc_server.controller;

import com.example.mc_server.dto.AuthRequest;
import com.example.mc_server.dto.CompanionList;
import com.example.mc_server.dto.PageResponse;
import com.example.mc_server.entity.User;
import com.example.mc_server.mapper.UserMapper;
import com.example.mc_server.service.CompanionService;
import com.example.mc_server.util.JwtUtil;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping
public class CompanionController {
    @Autowired
    private CompanionService  companionService;
    @Autowired
    private UserMapper userMapper;

    //设置陪护师
    @PostMapping("/set/companion")
    public AuthRequest setCompanion(@RequestBody Map<String, String> map, HttpServletRequest request) {
        Integer code = 500;
        String message = "failure";
        Object data = null;
        try {
            String token = request.getHeader("x-token");
            Long id = Long.parseLong(map.get("id"));
            String name = map.get("name");
            String mobile = map.get("mobile");
            String avatar = map.get("avatar");
            Integer age= Integer.parseInt(map.get("age"));
            String sex= map.get("sex");
            Integer active= Integer.parseInt(map.get("active"));

            Long userId = JwtUtil.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            if( id>0){
                //更新
                companionService.updateCompanion(id,name, mobile, age, sex, avatar, active);
                return new AuthRequest<>(10000,"success","创建成功");
            } else if (id==0) {
                //创建
                companionService.insertCompanion(name, mobile, age, sex, avatar, active, user.getId().intValue());
                return new AuthRequest<>(10000,"success","创建成功");
            }
        }catch (Exception e){
            message = e.getMessage();
        }

        return new AuthRequest<>(code,message,data);
    }

    //获取陪护师头像列表
    @GetMapping("/photo/list")
        public AuthRequest getPhotos(){
        Integer code = 500;
        String message = "failure";
        Object data = null;
        try {
            return new AuthRequest<>(10000,"success",companionService.selectPhotos());
        }catch (Exception e){
            message = e.getMessage();
        }
            return  new AuthRequest<>(code,message,data);
        }

    //获取陪护师列表
    @GetMapping("companion/list")
    public AuthRequest<PageResponse<CompanionList>> getCompanionList(
            @RequestParam(defaultValue = "1") Integer  pageNum,
            @RequestParam(defaultValue = "10") Integer  pageSize,
            HttpServletRequest request)
    {
        PageResponse<CompanionList> data = null;
        Integer code = 500;
        String message = "failure";
        try{
            String token = request.getHeader("x-token");
            Long userId = JwtUtil.getUserIdFromToken(token);
            PageInfo<CompanionList> page = companionService.getAllCompanionList(pageNum, pageSize, userId.intValue());
            data= new PageResponse<>(page.getList(), page.getTotal());
            code = 10000;
            message = "success";
        } catch (Exception e) {
            message = e.getMessage();
        }
        return new AuthRequest<>(code,message,data);
    }

    //删除陪护师
    // ... existing code ...

    //删除陪护师
    @PostMapping("/companion/delete")
    public AuthRequest deleteCompanion(@RequestBody Map<String, List<Map<String, Long>>> map) {
        try{
            List<Map<String, Long>> idList = map.get("id");
            if(idList == null || idList.isEmpty()){
                return new AuthRequest<>(400,"请选择要删除的陪护师",null);
            }

            List<Long> ids = idList.stream()
                    .map(item -> item.get("id"))
                    .collect(Collectors.toList());

            companionService.deleteCompanion(ids);
            return new AuthRequest<>(10000,"success","删除成功");
        } catch (Exception e) {
            return new AuthRequest<>(500,e.getMessage(),null);
        }
    }
}



