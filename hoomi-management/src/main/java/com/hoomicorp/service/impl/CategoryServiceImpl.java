package com.hoomicorp.service.impl;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.QuerySnapshot;
import com.hoomicorp.exception.EventNotFoundException;
import com.hoomicorp.model.dto.CategoryDto;
import com.hoomicorp.model.entity.Category;
import com.hoomicorp.repository.EntertainmentRepository;
import com.hoomicorp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final EntertainmentRepository entertainmentRepository;
    private final Firestore firestore;

    @Autowired
    public CategoryServiceImpl(EntertainmentRepository entertainmentRepository, Firestore firestore) {
        this.entertainmentRepository = entertainmentRepository;
        this.firestore = firestore;
    }

    public Category findById(final Long id) {
        return entertainmentRepository.findById(id).orElseThrow(() -> new EventNotFoundException(id));
    }

    @PostConstruct
    void updateFirestore() {
        CollectionReference events = firestore.collection("categories");

        final List<Category> allCategories = entertainmentRepository.findAll();
        allCategories.forEach(category -> {
            final List<String> tags = category.getTags()
                            .stream()
                            .map(c -> c.getName().name())
                            .collect(Collectors.toList());
            final CategoryDto dto = CategoryDto.builder()
                    .name(category.getName().toLowerCase())
                    .displayName(category.getName())
                    .imageLink(category.getImageLink())
                    .tags(tags)
                    .build();

            events.document(String.valueOf(category.getId()))
                    .set(dto);
        });

        events.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirestoreException e) {
                System.out.println();
            }
        });


    }
}
