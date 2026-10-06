package com.twittvl.backend.common.projection;

// Projection Interface
public interface IdCount {
    Long getId();
    Long getCount();
}

//for count by GROUP BY, normally Spring data JPA will gave Object[] array, but we only require ID and
//count that is why projection IdCount interface will project messy data to id and count to work with

//NOTE:
//Note interface based projection is actually like DTO just interface based is used to get only required
//ones for us (simpler) while DTO is for doing work on data beforehand
