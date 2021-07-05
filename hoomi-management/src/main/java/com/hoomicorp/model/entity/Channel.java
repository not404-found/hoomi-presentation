package com.hoomicorp.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString(exclude = {"subscribers", "streams", "owner"})
@EqualsAndHashCode
@AllArgsConstructor

@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private String id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "subscribers_count")
    private Integer subscribersCount;

    @Column(name = "is_live")
    private boolean isLive;

    @OneToMany(mappedBy = "channel")
    private List<Stream> streams = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "channels_subscribers",
            joinColumns = @JoinColumn(name = "channel_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> subscribers = new ArrayList<>();
}
