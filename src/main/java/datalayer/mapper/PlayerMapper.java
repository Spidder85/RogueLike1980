package datalayer.mapper;

import datalayer.dto.PlayerDTO;
import domain.Item;
import domain.character.Player;

public class PlayerMapper {
    public static PlayerDTO toDTO(Player p) {
        PlayerDTO dto = new PlayerDTO();
        dto.maxHealth = p.getMaxHealth();
        dto.health = p.getHealth();
        dto.agility = p.getAgility();
        dto.strength = p.getStrength();

        dto.x = p.getX();
        dto.y = p.getY();

        if (p.getCurrentWeapon() != null) {
            dto.currentWeapon = ItemMapper.toDTO(p.getCurrentWeapon());
        }
        dto.backpack = BackpackMapper.toDTO(p.getBackpack());
        return dto;
    }

    public static Player fromDTO(PlayerDTO dto) {
        Player player = new Player(
                dto.maxHealth,
                dto.agility,
                dto.strength
        );

        // 2. корректируем текущее здоровье (может быть < maxHealth)
        if (dto.health < dto.maxHealth) {
            player.takeDamage(dto.maxHealth - dto.health);
        }
        //  3. позиция
        player.setPosition(dto.x, dto.y);

        // 4. экипируем оружие
        if (dto.currentWeapon != null) {
            Item item = ItemMapper.fromDTO(dto.currentWeapon);
            player.setCurrentWeapon(item);
        }

        BackpackMapper.fromDTO(dto.backpack, player.getBackpack());
        return player;
    }
}
