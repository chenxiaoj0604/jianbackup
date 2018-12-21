package com.jianbackup.model;

import lombok.Data;

/**
 * Created by duanxun on 2018-08-24.
 */
@Data
public class Ftp {

    private String ip;

    private int port;

    private String username;

    private String password;

    private String dir;

}
