package br.com.todolist.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {

        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de inicio/termino deve ser maior do que a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("A data de inicio deve ser menor do que a data de término");
        }

        var task = this.taskRepository.save(taskModel);
        return  ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> listAllTasks(HttpServletRequest request){
        var idUser = request.getAttribute("idUser");
        var tasks =  this.taskRepository.findByIdUser((UUID) idUser);
        return tasks;
    }

   @PutMapping("/{id}")
    public ResponseEntity<TaskModel> updateTaskById(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) throws Exception {

        //var task = this.taskRepository.findById(id).orElse(null);
        //if(task == null){
        //    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        //            .body("Task not found");
        //}
        //var idUser = request.getAttribute("idUser");
        //if(!taskModel.getIdUser().equals(idUser));{
        //    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        //            .body("Unauthozied user");
        //}
        //Utils.copyNonNullProperties(taskModel, task);
        //return this.taskRepository.save(task);


        var idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);
        taskModel.setId(id);
        var taskOption = this.taskRepository.findByIdAndIdUser(id, UUID.fromString(idUser.toString()));
        if(taskOption.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var task = taskOption.get();
        task.setTitle(taskModel.getTitle());
        task.setPriority(taskModel.getPriority());
        task.setDescription(taskModel.getDescription());
        task.setStartAt(taskModel.getStartAt());
        task.setEndAt(taskModel.getEndAt());

        return ResponseEntity.ok(this.taskRepository.save(task));
    }

}
