package com.example.financeViewer.Assembler;

import com.example.financeViewer.Model.Transaction;
import com.example.financeViewer.Controller.TransactionController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TransactionModelAssembler implements RepresentationModelAssembler<Transaction, EntityModel<Transaction>> {

    @Override
    public EntityModel<Transaction> toModel(Transaction entity) {
        return EntityModel.of(entity, WebMvcLinkBuilder.linkTo(methodOn(TransactionController.class).all()).withRel("transactions"),
                linkTo(methodOn(TransactionController.class).one(entity.getId())).withSelfRel());
    }
}
