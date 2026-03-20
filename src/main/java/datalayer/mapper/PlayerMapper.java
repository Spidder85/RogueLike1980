package datalayer.mapper;

import datalayer.dto.PlayerDTO;
import domain.item.Item;
import domain.character.Player;

public class PlayerMapper {
    public static PlayerDTO toDTO(Player p) {
        return new PlayerDTO(
            p.getMaxHealth(),
            p.getHealth(),
            p.getAgility(),
            p.getStrength(),

            p.getCurrentWeapon() != null ? ItemMapper.toDTO(p.getCurrentWeapon()) : null,

            p.getX(),
            p.getY(),

            BackpackMapper.toDTO(p.getBackpack())
        );
    }

    public static Player fromDTO(PlayerDTO dto) {
        Player player = new Player(
                dto.maxHealth(),
                dto.agility(),
                dto.strength()
        );

        // 2. корректируем текущее здоровье (может быть < maxHealth)
        if (dto.health() < dto.maxHealth()) {
            player.takeDamage(dto.maxHealth() - dto.health());
        }
        //  3. позиция
        player.setPosition(dto.x(), dto.y());

        // 4. экипируем оружие
        if (dto.currentWeapon() != null) {
            Item item = ItemMapper.fromDTO(dto.currentWeapon());
            player.setCurrentWeapon(item);
        }

        BackpackMapper.fromDTO(dto.backpack(), player.getBackpack());
        return player;
    }
}
