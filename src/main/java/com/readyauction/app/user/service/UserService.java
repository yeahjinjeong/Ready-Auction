//package com.readyauction.app.user.service;
//
//import com.readyauction.app.common.handler.UserNotFoundException;
//import com.readyauction.app.user.entity.User;
//import com.readyauction.app.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    public User findUserById(Long id) {
//        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
//    }
//
//    @Transactional(readOnly = true)
//    public User findMemberByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    @Transactional
//    public User saveUser(User user) {
//        return userRepository.save(user);
//    }
//
////    @Transactional
////    public Member updateMember(Member member) {
////        Member existingMember = memberRepository.findById(member.getId())
////                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + member.getId()));
////        existingMember.setNickname(member.getNickname());
////        existingMember.setPicture(member.getPicture());
////        existingMember.setAddress(member.getAddress());
////        existingMember.setMannersScore(member.getMannersScore());
////        existingMember.setCashPoint(member.getCashPoint());
////        return memberRepository.save(existingMember);
////    }
//
//    @Transactional
//    public void deleteMember(Long id) {
//        userRepository.deleteById(id);
//    }
//}