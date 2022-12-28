package thaumcraft.common.golems.tasks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.seals.SealHandler;


public class TaskHandler
{
    static int TASK_LIMIT = 10000;
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Task>> tasks;
    
    public static void addTask(int dim, Task ticket) {
        if (!TaskHandler.tasks.containsKey(dim)) {
            TaskHandler.tasks.put(dim, new ConcurrentHashMap<Integer, Task>());
        }
        ConcurrentHashMap<Integer, Task> dc = TaskHandler.tasks.get(dim);
        if (dc.size() > 10000) {
            try {
                Iterator<Task> i = dc.values().iterator();
                if (i.hasNext()) {
                    i.next();
                    i.remove();
                }
            }
            catch (Exception ex) {}
        }
        dc.put(ticket.getId(), ticket);
    }
    
    public static Task getTask(int dim, int id) {
        return getTasks(dim).get(id);
    }
    
    public static ConcurrentHashMap<Integer, Task> getTasks(int dim) {
        if (!TaskHandler.tasks.containsKey(dim)) {
            TaskHandler.tasks.put(dim, new ConcurrentHashMap<Integer, Task>());
        }
        return TaskHandler.tasks.get(dim);
    }
    
    public static ArrayList<Task> getBlockTasksSorted(int dim, UUID uuid, Entity golem) {
        ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
        ArrayList<Task> out = new ArrayList<Task>();
    Label_0025:
        for (Task ticket : tickets.values()) {
            if (!ticket.isReserved()) {
                if (ticket.getType() != 0) {
                    continue;
                }
                if (uuid != null && ticket.getGolemUUID() != null && !uuid.equals(ticket.getGolemUUID())) {
                    continue;
                }
                if (out.size() == 0) {
                    out.add(ticket);
                }
                else {
                    double d = ticket.getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                    d -= ticket.getPriority() * 256;
                    for (int a = 0; a < out.size(); ++a) {
                        double d2 = out.get(a).getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                        d2 -= out.get(a).getPriority() * 256;
                        if (d < d2) {
                            out.add(a, ticket);
                            continue Label_0025;
                        }
                    }
                    out.add(ticket);
                }
            }
        }
        return out;
    }
    
    public static ArrayList<Task> getEntityTasksSorted(int dim, UUID uuid, Entity golem) {
        ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
        ArrayList<Task> out = new ArrayList<Task>();
    Label_0025:
        for (Task ticket : tickets.values()) {
            if (!ticket.isReserved()) {
                if (ticket.getType() != 1) {
                    continue;
                }
                if (uuid != null && ticket.getGolemUUID() != null && !uuid.equals(ticket.getGolemUUID())) {
                    continue;
                }
                if (ticket.getEntity() == null || ticket.getEntity().isDead) {
                    ticket.setSuspended(true);
                }
                else if (out.size() == 0) {
                    out.add(ticket);
                }
                else {
                    double d = ticket.getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                    d -= ticket.getPriority() * 256;
                    for (int a = 0; a < out.size(); ++a) {
                        double d2 = out.get(a).getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                        d2 -= out.get(a).getPriority() * 256;
                        if (d < d2) {
                            out.add(a, ticket);
                            continue Label_0025;
                        }
                    }
                    out.add(ticket);
                }
            }
        }
        return out;
    }
    
    public static void completeTask(Task task, EntityThaumcraftGolem golem) {
        if (task.isCompleted() || task.isSuspended()) {
            return;
        }
        ISealEntity se = SealHandler.getSealEntity(golem.world.provider.getDimension(), task.getSealPos());
        if (se != null) {
            task.setCompletion(se.getSeal().onTaskCompletion(golem.world, golem, task));
        }
        else {
            task.setCompletion(true);
        }
    }
    
    public static void clearSuspendedOrExpiredTasks(World world) {
        ConcurrentHashMap<Integer, Task> tickets = getTasks(world.provider.getDimension());
        ConcurrentHashMap<Integer, Task> temp = new ConcurrentHashMap<Integer, Task>();
        for (Task ticket : tickets.values()) {
            if (!ticket.isSuspended() && ticket.getLifespan() > 0L) {
                ticket.setLifespan((short)(ticket.getLifespan() - 1L));
                temp.put(ticket.getId(), ticket);
            }
            else {
                ISealEntity sEnt = SealHandler.getSealEntity(world.provider.getDimension(), ticket.getSealPos());
                if (sEnt == null) {
                    continue;
                }
                sEnt.getSeal().onTaskSuspension(world, ticket);
            }
        }
        TaskHandler.tasks.put(world.provider.getDimension(), temp);
    }
    
    static {
        TaskHandler.tasks = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Task>>();
    }
}
