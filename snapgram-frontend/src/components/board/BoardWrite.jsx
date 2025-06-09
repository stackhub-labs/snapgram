import Input from "../common/Input";
import Button from "../common/Button";
import "../pages/BoardWritePage.css";

const BoardWrite = () => {
    return (
        <div className ="input-tag-outline">
        <div className="input-tag">
            <Input placeholder={"게시글 입력란"}/>
        </div>
        <div>
            <Button text={"작성"}/>
        </div>
        </div>
    );
}
export default BoardWrite;