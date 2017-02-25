package com.barry.julien.docker.tools.response;

import lombok.Builder;
import lombok.Data;

/**
 * Created by gaoqingyang on 2/24/17.
 */
@Data
@Builder
public class Container
{

    String name;
    String imageName;

}
