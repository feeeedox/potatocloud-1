package net.potatocloud.webinterface.api.rest;

import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import net.potatocloud.webinterface.dto.group.CreateGroupRequestDto;
import net.potatocloud.webinterface.dto.group.UpdateGroupRequestDto;
import net.potatocloud.webinterface.service.GroupService;

@RequiredArgsConstructor
public class GroupRestController extends BaseRestController {

    private final GroupService groupService;

    @Override
    public void register() {
        ApiBuilder.path("/api/groups", () -> {
            ApiBuilder.get(ctx -> ctx.json(groupService.getAllGroups()));

            ApiBuilder.post(ctx -> {
                CreateGroupRequestDto request = ctx.bodyAsClass(CreateGroupRequestDto.class);
                if (!groupService.createGroup(request)) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(error("Unable to create group"));
                    return;
                }
                ctx.status(HttpStatus.CREATED);
            });

            ApiBuilder.get("/{name}", ctx -> {
                String name = ctx.pathParam("name");
                var group = groupService.getGroupByName(name);
                if (group == null) {
                    ctx.status(HttpStatus.NOT_FOUND).json(error("Group '" + name + "' not found"));
                    return;
                }
                ctx.json(group);
            });

            ApiBuilder.post("/{name}/start", ctx -> {
                String name = ctx.pathParam("name");
                if (!groupService.startGroup(name)) {
                    ctx.status(HttpStatus.NOT_FOUND).json(error("Group '" + name + "' not found"));
                    return;
                }
                ctx.status(HttpStatus.NO_CONTENT);
            });

            ApiBuilder.post("/{name}/update", ctx -> {
                String name = ctx.pathParam("name");

                if (!groupService.exists(name)) {
                    ctx.status(HttpStatus.NOT_FOUND).json(error("Group '" + name + "' not found"));
                }

                UpdateGroupRequestDto dto = ctx.bodyAsClass(UpdateGroupRequestDto.class);

                if (!groupService.updateGroup(dto)) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(error("Unable to update group"));
                    return;
                }

                ctx.status(HttpStatus.NO_CONTENT);
            });

            ApiBuilder.post("/{name}/stopAll", ctx -> {
                String name = ctx.pathParam("name");
                if (!groupService.stopAllInGroup(name)) {
                    ctx.status(HttpStatus.NOT_FOUND).json(error("Group '" + name + "' not found"));
                    return;
                }
                ctx.status(HttpStatus.NO_CONTENT);
            });
        });
    }
}
