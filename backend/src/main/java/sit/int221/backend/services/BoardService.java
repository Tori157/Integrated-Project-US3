package sit.int221.backend.services;

import io.viascom.nanoid.NanoId;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import sit.int221.backend.dtos.BoardDTO;
import sit.int221.backend.dtos.BoardOwnerDTO;
import sit.int221.backend.exceptions.NotFoundException;
import sit.int221.backend.project_management.Board;
import sit.int221.backend.project_management.BoardRepository;
import sit.int221.backend.user_account.User;

import java.util.*;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final StatusService statusService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public List<BoardDTO> getAllBoardsByUserId(String userId) {
        List<Board> allBoards = boardRepository.findAllByOwnerId(userId);
        User owner = userService.getUserById(userId).orElse(null);
        return allBoards.stream().map(board -> convertToBoardDTO(board, owner)).toList();
    }

    public BoardDTO getBoardByUserAndId(String userId, String boardId) {
        if (!userHasAccessToBoard(userId, boardId)) {
            throw new AccessDeniedException("User does not have access to this board.");
        }

        User owner = userService.getUserById(userId).orElse(null);
        return boardRepository.findByIdAndOwnerId(boardId, userId)
                .map(board -> convertToBoardDTO(board, owner))
                .orElseThrow(() -> new NotFoundException("Board id '" + boardId + "' not found"));
    }

    public Optional<Board> getBoardById(String boardId) {
        return boardRepository.findById(boardId);
    }

    private BoardDTO convertToBoardDTO(Board board, User owner) {
        BoardDTO boardDTO = modelMapper.map(board, BoardDTO.class);
        boardDTO.setOwner(Optional.ofNullable(owner).map(user -> modelMapper.map(user, BoardOwnerDTO.class)));
        return boardDTO;
    }

    private boolean userHasAccessToBoard(String userId, String boardId) {
        return boardRepository.findByIdAndOwnerId(boardId, userId).isPresent();
    }

    public BoardDTO createBoard(BoardDTO boardDto, String userId) {
        User currentUser = userService.getUserById(userId).orElse(null);
        Board newBoard = Board.builder().id(NanoId.generate(10)).name(boardDto.getName()).ownerId(userId).build();
        Board savedBoard = boardRepository.save(newBoard);
        statusService.createDefaultStatuses(savedBoard);
        return convertToBoardDTO(savedBoard, currentUser);
    }

}



