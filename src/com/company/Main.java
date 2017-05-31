package com.company;

import facebook4j.*;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class Main {

     static Reading reading=new Reading();
    public static Facebook facebook;
    public static  Configuration createConfiguration() {
        ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        confBuilder.setDebugEnabled(true);
        confBuilder.setOAuthAppId("************");
        confBuilder.setOAuthAppSecret("****************");
        confBuilder.setOAuthAccessToken("************************************************************************************************");
        confBuilder.setUseSSL(true);
        confBuilder.setJSONStoreEnabled(true);
        Configuration configuration = confBuilder.build();
        return configuration;
    }
    public static ArrayList<User> getUsersFromComments(ResponseList<Post> posts) throws FacebookException{
        ArrayList<User> users=new ArrayList<>();
        PagableList<Comment> comments;
        for(Post post: posts){
            comments=post.getComments();
            for(Comment com: comments){
                users.add(facebook.getUser(com.getFrom().getId()));
            }
        }
        return users;
    }
    public static ArrayList<User> getUsersFromLikes(ResponseList<Post> posts) throws FacebookException {
        ArrayList<User> users=new ArrayList<>();
        PagableList<Like> likes;
        for(Post post: posts){
            likes=post.getLikes();
            for(Like like:likes){
                users.add(facebook.getUser(like.getId()));
            }
        }
        return users;
    }

    public static void write(String fileName, String text) {
        File file = new File(fileName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
                out.print(text);
            } finally {
                out.close();
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String userRow(User user){
        StringBuilder us=new StringBuilder();
        if (user.getEmail() != null) us.append(user.getEmail() + " ");
        if (user.getFirstName() != null) us.append(user.getFirstName() + " ");
        if (user.getLastName() != null) us.append(user.getLastName() + " ");
        if (user.getLocation() != null) us.append(user.getLocation().toString() + " ");
        if (user.getBirthday() != null) us.append(user.getBirthday() + " ");
        if (user.getGender() != null) us.append(user.getGender() + " ");
        if (user.getAgeRange()!= null) us.append(user.getAgeRange().toString() + " ");
        if (user.getId() != null) us.append(user.getId() + " ");
        us.append("\r\n");
        return us.toString();
    }

    public static void main(String[] args) {
        FacebookFactory ff = new FacebookFactory(createConfiguration());
        facebook = ff.getInstance();
            Scanner in = new Scanner(System.in);
        while(true) {
            System.out.println("Enter page id:");
            String id = in.next();
            System.out.println("Enter full name of file to save");
            String path = in.next();
            System.out.println("Enter count of last posts you want to parse");
            int limit = in.nextInt();
            reading = new Reading().limit(limit);
            ArrayList<User> users = new ArrayList<User>();
            try {
                System.out.println("Wait please..");
                ResponseList<Post> feed = facebook.getFeed(id, reading);
                users.addAll(getUsersFromLikes(feed));
                users.addAll(getUsersFromComments(feed));
                System.out.println(users.size() + " unique users were found: ");
                System.out.println();
                Set<User> se = new HashSet<>(users);
                StringBuilder u = new StringBuilder();
                for (User user : se) {
                    System.out.println(userRow(user));
                    u.append(userRow(user));
                }
                write(path, u.toString());
                System.out.println("Saving complete");
                System.out.println("__________________________________________________________");
            } catch (FacebookException e) {
                e.printStackTrace();
            }
        }
    }
}
