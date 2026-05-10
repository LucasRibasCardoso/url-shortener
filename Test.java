import java.util.UUID;
class UrlDetailsCommand {}
class DeleteUrlCommand {}
interface TestMapper {
  UrlDetailsCommand toCommand(UUID requesterId, String shortCode, boolean canReadAny);
  DeleteUrlCommand toCommand(UUID requesterId, String shortCode, boolean canDeleteAny);
}
